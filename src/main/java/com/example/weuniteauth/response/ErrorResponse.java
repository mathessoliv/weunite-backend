package com.example.weuniteauth.response;

import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class ErrorResponse {
    private String message;
    private String errorCode;
    private LocalDateTime timestamp;

    public ErrorResponse(String message, String errorCode) {
        this.message = message;
        this.errorCode = errorCode;
        this.timestamp = LocalDateTime.now();
    }
}
