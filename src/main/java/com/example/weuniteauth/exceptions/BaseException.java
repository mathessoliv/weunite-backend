package com.example.weuniteauth.exceptions;

import lombok.Getter;

@Getter
public class BaseException extends RuntimeException {
    private final String errorCode;

    public BaseException(String message, String errorCode) {
        super(message);
        this.errorCode = errorCode;
    }
}
