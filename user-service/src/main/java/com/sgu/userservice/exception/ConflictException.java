package com.sgu.userservice.exception;

public class ConflictException extends RuntimeException {
    public ConflictException(String format) {
        super(format);
    }
}
