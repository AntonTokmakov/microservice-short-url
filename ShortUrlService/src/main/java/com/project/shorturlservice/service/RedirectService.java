package com.project.shorturlservice.service;

import jakarta.servlet.http.HttpServletResponse;

public interface RedirectService {
    void redirectTo(String shortUrl, HttpServletResponse response);

}
