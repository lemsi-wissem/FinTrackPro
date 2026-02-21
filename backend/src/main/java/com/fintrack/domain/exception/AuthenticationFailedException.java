package com.fintrack.domain.exception;

public class AuthenticationFailedException extends RuntimeException {
    public AuthenticationFailedException() {
        super("Invalid email or password");
    }
}
