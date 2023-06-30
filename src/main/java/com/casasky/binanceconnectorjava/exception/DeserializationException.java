package com.casasky.binanceconnectorjava.exception;

public class DeserializationException extends RuntimeException {

    public DeserializationException() {
        super("unable to deserialize response");
    }
}
