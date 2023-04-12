package com.sgu.orderservice.exception;

public class BadGateWayException extends RuntimeException {
    public BadGateWayException(String message) {
        super(message);
    }
}
