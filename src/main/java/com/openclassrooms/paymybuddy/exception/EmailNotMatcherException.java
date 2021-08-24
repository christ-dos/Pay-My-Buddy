package com.openclassrooms.paymybuddy.exception;

public class EmailNotMatcherException extends RuntimeException {
    public EmailNotMatcherException(String message) {
        super(message);
    }
}
