package com.sgu.orderservice.exception;

public class ConflictException extends RuntimeException {
    public ConflictException(String string) {
        super(string);
    }
}
