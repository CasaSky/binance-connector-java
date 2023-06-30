package com.casasky.binanceconnectorjava.tool;

import com.casasky.binanceconnectorjava.model.Symbol;

import java.net.URI;
import java.time.Duration;
import java.time.Instant;

import static java.lang.String.format;

public class BinanceApiTool {

    private static final String BINANCE_BASE_API = "https://api.binance.com";
    private static final String PRICE_ENDPOINT = BINANCE_BASE_API + "/api/v3/ticker/price?symbol=";
    private static final String WALLET_SNAPSHOT_ENDPOINT = BINANCE_BASE_API + "/sapi/v1/accountSnapshot?";
    private static final String WALLET_SNAPSHOT_PARAMS = "type=SPOT&limit=1&recvWindow=5000&startTime=%s&endTime=%s&timestamp=%s";

    private BinanceApiTool() {
    }

    public static URI priceUri(Symbol symbol) {
        return URI.create(PRICE_ENDPOINT + symbol.name());
    }

    public static URI walletSnapshotUri(String binanceSecretKey) {
        return URI.create(WALLET_SNAPSHOT_ENDPOINT + paramsWithSignature(params(), binanceSecretKey));
    }

    private static String params() {
        return format(WALLET_SNAPSHOT_PARAMS, startTime(), endTime(), currentTime());
    }

    private static long currentTime() {
        return Instant.now().toEpochMilli();
    }

    private static long startTime() {
        return Instant.now().minus(Duration.ofHours(12)).toEpochMilli();
    }

    private static long endTime() {
        return Instant.now().toEpochMilli();
    }

    private static String paramsWithSignature(String params, String binanceSecretKey) {
        return params + format("&signature=%s", SignatureTool.sign(params, binanceSecretKey));
    }

}
