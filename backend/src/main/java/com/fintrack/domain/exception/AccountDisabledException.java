package com.fintrack.domain.exception;

public class AccountDisabledException extends RuntimeException {
    public AccountDisabledException() {
        super("Account has been deactivated");
    }
}
