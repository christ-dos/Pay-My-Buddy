package com.openclassrooms.paymybuddy.exception;

/**
 * Class that handles exception password not match with field confirm password
 *
 * @author Christine Duarte
 */
public class PasswordNotMatcherException extends RuntimeException {
    /**
     * Constructor
     *
     * @param message A string containing the message that is sent if the exception in handling
     */
    public PasswordNotMatcherException(String message) {
        super(message);
    }
}
