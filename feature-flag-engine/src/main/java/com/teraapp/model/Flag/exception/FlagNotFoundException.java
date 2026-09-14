package com.teraapp.model.Flag.exception;

public class FlagNotFoundException extends RuntimeException {
    public FlagNotFoundException(String message) {
        super(message);
    }
}