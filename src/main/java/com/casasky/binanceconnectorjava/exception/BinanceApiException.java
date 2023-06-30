package com.casasky.binanceconnectorjava.exception;

public class BinanceApiException extends RuntimeException {

    public BinanceApiException() {
        super("unable to receive response from binance api");
    }
}
