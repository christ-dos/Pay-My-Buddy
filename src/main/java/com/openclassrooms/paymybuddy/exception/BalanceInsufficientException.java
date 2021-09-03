package com.openclassrooms.paymybuddy.exception;

/**
 * Class that handles exception when the balance is insufficient
 *
 * @author Christine Duarte
 */
public class BalanceInsufficientException extends RuntimeException {
    /**
     * Constructor
     *
     * @param message A string containing the message that is sent if the exception in handling
     */
    public BalanceInsufficientException(String message) {
        super(message);
    }
}
