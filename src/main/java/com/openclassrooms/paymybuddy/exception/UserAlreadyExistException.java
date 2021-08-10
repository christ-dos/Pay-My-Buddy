package com.openclassrooms.paymybuddy.exception;

/**
 * Class that handles exceptions when the user already exist in list of connections
 *
 * @author Christine Duarte
 */
public class UserAlreadyExistException extends RuntimeException {

    /**
     * Constructor
     *
     * @param message A string containing the message that is sent if the exception in handling
     */
    public UserAlreadyExistException(String message) {
        super(message);
    }
}
