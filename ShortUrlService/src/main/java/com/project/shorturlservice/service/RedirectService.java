package com.project.shorturlservice.service;

import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;

public interface RedirectService {
    HttpStatus redirectTo(String shortUrl, HttpServletResponse response);

}
