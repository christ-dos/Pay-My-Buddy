package com.openclassrooms.paymybuddy.exception;

public class BalanceInsufficientException extends RuntimeException {

    public BalanceInsufficientException(String message) {
        super(message);
    }
}
