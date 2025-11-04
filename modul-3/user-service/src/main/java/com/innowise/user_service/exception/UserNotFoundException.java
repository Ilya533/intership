package com.innowise.user_service.exception;

public class UserNotFoundException extends RuntimeException {
    public UserNotFoundException(Long id) {
        super("Id: " + id + " not found");
    }

    public UserNotFoundException(String email) {
        super("Email" + email + " not found ");
    }

}
