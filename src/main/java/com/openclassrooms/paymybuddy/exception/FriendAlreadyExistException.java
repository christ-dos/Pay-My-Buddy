package com.openclassrooms.paymybuddy.exception;

public class FriendAlreadyExistException extends RuntimeException {
    public FriendAlreadyExistException(String message) {
        super(message);
    }
}
