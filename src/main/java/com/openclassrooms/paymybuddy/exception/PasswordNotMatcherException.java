package com.openclassrooms.paymybuddy.exception;

public class PasswordNotMatcherException extends RuntimeException {

    public PasswordNotMatcherException(String message) {
        super(message);
    }
}
