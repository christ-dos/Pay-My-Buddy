package com.openclassrooms.paymybuddy.exception;

/**
 * Class that handles exceptions when the user not exist in the database
 *
 * @author Christine Duarte
 */
public class UserNotFoundException extends RuntimeException {
    /**
     * Constructor
     *
     * @param message A string containing the message that is sent if the exception in handling
     */
    public UserNotFoundException(String message) {
        super(message);
    }
}
