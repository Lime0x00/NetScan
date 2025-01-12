package com.lime0x00.exceptions;

public class InvalidIPAddressException extends RuntimeException {
    public InvalidIPAddressException(String message) {
        super(message);
    }
}
