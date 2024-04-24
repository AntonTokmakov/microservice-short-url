package com.project.apigateway.controller;

import com.project.apigateway.controller.dto.ExceptionResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.net.ConnectException;
import lombok.extern.slf4j.Slf4j;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    @ExceptionHandler(ConnectException.class)
    public Mono<ResponseEntity<ExceptionResponse>> handleConnectException(ServerWebExchange request,
                                                                        Exception exception) {
        return Mono.just(getResponse(request, exception, HttpStatus.BAD_GATEWAY));
    }


    private ResponseEntity<ExceptionResponse> getResponse(ServerWebExchange request, Exception exception, HttpStatus status) {
        log.warn(exception.getClass().getName() + ": " + exception.getMessage());
        return ResponseEntity.status(status)
                .body(new ExceptionResponse(request.getRequest().getURI().toString(), exception.getMessage()));
    }

}
