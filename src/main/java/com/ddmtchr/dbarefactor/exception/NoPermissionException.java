package com.ddmtchr.dbarefactor.exception;

public class NoPermissionException extends RuntimeException {

    public NoPermissionException(String msg) {
        super(msg);
    }
}
