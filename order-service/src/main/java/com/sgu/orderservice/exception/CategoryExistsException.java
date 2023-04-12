package com.sgu.orderservice.exception;

public class CategoryExistsException extends RuntimeException {
    public CategoryExistsException(String s) {
        super(s);
    }
}
