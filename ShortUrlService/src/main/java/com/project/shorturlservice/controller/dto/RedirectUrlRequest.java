package com.project.shorturlservice.controller.dto;


import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.hibernate.validator.constraints.URL;

public record RedirectUrlRequest(
        @NotBlank(message = "Error, field longUrl cannot is empty")
        @NotNull(message = "Error, field longUrl cannot is null")
        @URL(message = "Error, field longUrl is not valid")
        String shortUrl) {
}
