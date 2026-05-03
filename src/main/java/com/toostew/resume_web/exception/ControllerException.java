package com.toostew.resume_web.exception;

public class ControllerException extends RuntimeException {
    public ControllerException(String message, Throwable cause) {
        super(message, cause);
    }
}
