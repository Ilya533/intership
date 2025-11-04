package com.innowise.user_service.exception;

public class CardNotFoundException extends RuntimeException {

    public CardNotFoundException(Long id) {
        super("Id=" + id + " not found ");
    }
}
