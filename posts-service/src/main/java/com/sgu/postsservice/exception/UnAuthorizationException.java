package com.sgu.postsservice.exception;

public class UnAuthorizationException extends RuntimeException {
    public UnAuthorizationException(String s) {
        super(s);
    }
}
