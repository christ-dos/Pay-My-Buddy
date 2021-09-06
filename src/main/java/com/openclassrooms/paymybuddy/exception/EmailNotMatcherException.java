package com.openclassrooms.paymybuddy.exception;

/**
 * Class that handles exception email not match with field confirm email
 *
 * @author Christine Duarte
 */
public class EmailNotMatcherException extends RuntimeException {
    /**
     * Constructor
     *
     * @param message A string containing the message that is sent if the exception in handling
     */
    public EmailNotMatcherException(String message) {
        super(message);
    }
}
