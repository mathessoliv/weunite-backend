package com.example.weuniteauth.exceptions;

public class DuplicateResourceException extends BaseException{
    public DuplicateResourceException(String resource){
        super(String.format("%s já existe", resource), "DUPLICATE_RESOURCE_EXCEPTION");
    }
}
