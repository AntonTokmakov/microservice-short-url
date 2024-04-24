package com.project.shorturlservice.controller.dto;

import org.springframework.http.HttpStatus;

public record ExceptionResponse(String timestamp, String path, String message, HttpStatus status) {
}
