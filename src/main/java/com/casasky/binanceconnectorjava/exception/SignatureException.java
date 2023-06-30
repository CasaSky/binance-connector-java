package com.casasky.binanceconnectorjava.exception;

public class SignatureException extends RuntimeException {

    public SignatureException() {
        super("unable to sign data");
    }
}
