package com.example.weuniteauth.exceptions;

public class NotFoundResourceException extends BaseException {
    public NotFoundResourceException(String resource, String identifier) {
        super(String.format("%s não encontrado com identificador %s", resource, identifier), "RESOURCE_NOT_FOUND");
    }
}
