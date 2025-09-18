package com.ddmtchr.dbarefactor.exception;

public class InsufficientFundsException extends RuntimeException {

    public InsufficientFundsException(String msg) {
        super(msg);
    }
}
