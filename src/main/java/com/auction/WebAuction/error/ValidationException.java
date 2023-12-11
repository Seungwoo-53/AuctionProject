package com.auction.WebAuction.error;

public class ValidationException extends RuntimeException {

    public ValidationException(String message) {
        super(message);
    }
}