package com.project.shorturlservice.exception;

public class LifeTimeExpiredException extends BaseRuntimeException {
    public LifeTimeExpiredException(String message) {
        super(message);
    }
}
