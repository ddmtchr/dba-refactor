package com.ddmtchr.dbarefactor.exception;

public class InconsistentRequestException extends RuntimeException {
    public InconsistentRequestException(String message) {
        super(message);
    }
}
