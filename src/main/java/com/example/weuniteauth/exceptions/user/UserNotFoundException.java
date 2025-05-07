package com.example.weuniteauth.exceptions.user;

import com.example.weuniteauth.exceptions.NotFoundResourceException;

public class UserNotFoundException extends NotFoundResourceException {
    public UserNotFoundException(String resource, String identifier) {
        super(resource, identifier);
    }
}
