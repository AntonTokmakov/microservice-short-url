package com.project.shorturlservice.exception;

public class LinkNotFoundException extends BaseRuntimeException {
    public LinkNotFoundException(String message) {
        super(message);
    }
}
