package com.toostew.resume_web.exception;

public class R2ServiceException extends RuntimeException {
    public R2ServiceException(String message,  Throwable cause) {
        super(message, cause);
    }
}
