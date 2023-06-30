package com.casasky.binanceconnectorjava;

import com.casasky.binanceconnectorjava.exception.BinanceApiException;
import com.casasky.binanceconnectorjava.exception.DeserializationException;
import com.casasky.binanceconnectorjava.model.AccountSnapshot;
import com.casasky.binanceconnectorjava.model.Asset;
import com.casasky.binanceconnectorjava.model.Price;
import com.casasky.binanceconnectorjava.model.Symbol;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.math.BigDecimal;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.Comparator;
import java.util.Optional;

import static com.casasky.binanceconnectorjava.tool.BinanceApiTool.priceUri;
import static com.casasky.binanceconnectorjava.tool.BinanceApiTool.walletSnapshotUri;

@Component
class BinanceConnector {

    @Value("${binance-api-key}")
    private String binanceApiKey;

    @Value("${binance-secret-key}")
    private String binanceSecretKey;

    Price price(Symbol symbol) {
        return deserialize(sendRequest(priceUri(symbol)), Price.class);
    }

    AccountSnapshot walletSnapshot(Optional<Asset> asset, Optional<Integer> limit) {
        return accountSnapshot(sendRequest(walletSnapshotUri(binanceSecretKey), "X-MBX-APIKEY", binanceApiKey), asset, limit);
    }

    private static HttpRequest httpRequest(URI uri, String... headers) {
        var requestBuilder = HttpRequest.newBuilder()
                .uri(uri)
                .GET();
        return headers.length == 0 ? requestBuilder.build() : requestBuilder.headers(headers).build();
    }

    private static String sendRequest(URI uri, String... headers) {
        try {
            return HttpClient.newBuilder()
                    .connectTimeout(Duration.ofSeconds(10))
                    .build()
                    .send(httpRequest(uri, headers), HttpResponse.BodyHandlers.ofString()).body();
        } catch (IOException | InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new BinanceApiException();
        }
    }

    private AccountSnapshot accountSnapshot(String body, Optional<Asset> assetParam, Optional<Integer> limit) {
        var deserializedBody = deserialize(body, ObjectNode.class);
        AccountSnapshot accountSnapshot = Optional.ofNullable(deserializedBody)
                .map(node -> node.get("snapshotVos"))
                .map(snapshotVosList -> snapshotVosList.get(0))
                .map(snapshotVos -> snapshotVos.get("data"))
                .map(data -> deserialize(data.toString(), AccountSnapshot.class))
                .orElseThrow(DeserializationException::new);

        return new AccountSnapshot(btcToEuro(accountSnapshot.totalAssetOfBtc()), accountSnapshot.totalAssetOfBtc(), accountSnapshot.balances()
                .stream()
                .filter(AccountSnapshot.Balance::isPositive)
                .filter(b -> assetParam.map(b::isAssetEqualTo).orElse(true))
                .sorted(Comparator.comparing(AccountSnapshot.Balance::free).reversed())
                .limit(limit.orElse(Integer.MAX_VALUE))
                .toList());
    }

    BigDecimal btcToEuro(BigDecimal totalAssetOfBtc) {
        return totalAssetOfBtc.multiply(price(Symbol.BTCEUR).price());
    }

    private static <T> T deserialize(String message, Class<T> clazz) {
        try {
            return new ObjectMapper().readValue(message, clazz);
        } catch (JsonProcessingException e) {
            throw new DeserializationException();
        }
    }

}
