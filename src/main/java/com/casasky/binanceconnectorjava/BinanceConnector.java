package com.casasky.binanceconnectorjava;

import com.casasky.binanceconnectorjava.exception.BinanceApiException;
import com.casasky.binanceconnectorjava.exception.DeserializationException;
import com.casasky.binanceconnectorjava.model.AccountSnapshot;
import com.casasky.binanceconnectorjava.model.AccountSnapshot.Balance;
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
import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Stream;

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

    AccountSnapshot retrieveWalletSnapshotByAsset(Asset asset, Integer limit) {
        return accountSnapshotMapperByAsset(sendRequest(walletSnapshotUri(binanceSecretKey), "X-MBX-APIKEY", binanceApiKey), asset, limit);
    }

    AccountSnapshot retrieveWalletSnapshot() {
        return accountSnapshotMapper(sendRequest(walletSnapshotUri(binanceSecretKey), "X-MBX-APIKEY", binanceApiKey));
    }

    private AccountSnapshot accountSnapshotMapper(String rawData) {
        return accountSnapshotTransformer(rawData, BinanceConnector::normalizedBalance);
    }

    private AccountSnapshot accountSnapshotMapperByAsset(String body, Asset assetParam, Integer limit) {
        return accountSnapshotTransformer(body, accountSnapshot -> normalizedBalance(accountSnapshot).filter(b -> b.isAssetEqualTo(assetParam))
                .limit(limit));
    }

    private AccountSnapshot accountSnapshotTransformer(String body, Function<AccountSnapshot, Stream<Balance>> consumer) {
        AccountSnapshot accountSnapshot = deserializeAccountSnapshot(body);
        List<Balance> balance = consumer.apply(accountSnapshot).toList();
        return new AccountSnapshot(btcToEuro(accountSnapshot.totalAssetOfBtc()), accountSnapshot.totalAssetOfBtc(), balance);
    }

    private static AccountSnapshot deserializeAccountSnapshot(String body) {
        var deserializedBody = deserialize(body, ObjectNode.class);
        return Optional.ofNullable(deserializedBody)
                .map(node -> node.get("snapshotVos"))
                .map(snapshotVosList -> snapshotVosList.get(0))
                .map(snapshotVos -> snapshotVos.get("data"))
                .map(data -> deserialize(data.toString(), AccountSnapshot.class))
                .orElseThrow(DeserializationException::new);
    }

    private static Stream<Balance> normalizedBalance(AccountSnapshot accountSnapshot) {
        return accountSnapshot.balances()
                .stream()
                .filter(Balance::isPositive)
                .sorted(Comparator.comparing(Balance::free).reversed());
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

}
