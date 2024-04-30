package com.project.shorturlservice.service.impl;

import com.project.shorturlservice.exception.GeneratedUrlException;
import com.project.shorturlservice.exception.LifeTimeExpiredException;
import com.project.shorturlservice.exception.LinkNotFoundException;
import com.project.shorturlservice.exception.RedirectException;
import com.project.shorturlservice.model.Url;
import com.project.shorturlservice.repository.UrlRepository;
import com.project.shorturlservice.service.FindLink;
import com.project.shorturlservice.service.GeneratorService;
import com.project.shorturlservice.service.RedirectService;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.ZonedDateTime;
import java.util.Base64;

@Service
@RequiredArgsConstructor
@Slf4j
public class UrlService implements GeneratorService, RedirectService, FindLink {

    private final UrlRepository urlRepository;

    @Value("${life.url.minutes:10}")
    private int lifeUrlMinutes;

    @Value("${app.url.prefix:http://localhost:8090/api/v1/}")
    private String prefixUrl;

    @Override
    public String generateShortUrl(String longUrl) {

        if (longUrl == null || longUrl.isEmpty()) {
            throw new IllegalArgumentException("Long URL cannot be empty");
        }

        if (urlRepository.existsByLongUrl(longUrl)) {
            Url url = urlRepository.findByLongUrl(longUrl).get();
            url.setDateTime(ZonedDateTime.now());
            urlRepository.save(url);
            log.info("Update Short URL: %s long URL: %s".formatted(url.getShortUrl(), longUrl));
            return prefixUrl + url.getShortUrl();
        }

        String shortUrl = getDecoderShortUrl(longUrl);
        urlRepository.save(new Url(longUrl, shortUrl, ZonedDateTime.now()));
        log.info("Create Short URL: %s long URL: %s".formatted(prefixUrl + shortUrl,  longUrl));
        return prefixUrl + shortUrl;
    }

    @Override
    public void redirectTo(String shortUrl, HttpServletResponse response) {

        Url url = urlRepository.findByShortUrl(shortUrl)
                .orElseThrow(() -> new LinkNotFoundException("Short URL %s not found"
                        .formatted(prefixUrl + shortUrl)));

        isExpired(url);

        try {
            response.sendRedirect(url.getLongUrl());
        } catch (IOException e) {
            log.warn("Failed to redirect to %s".formatted(url.getLongUrl()) + ": " + e.getMessage());
            throw new RedirectException("Failed to redirect to %s".formatted(prefixUrl + url.getLongUrl()));
        }
    }

    @Override
    public String getLongUrl(String shortUrl) {
        Url url = urlRepository.findByShortUrl(shortUrl)
                .orElseThrow(() -> new LinkNotFoundException("Short URL %s not found"
                        .formatted(shortUrl)));
        isExpired(url);
        log.info("Get Long URL: %s short URL: %s".formatted(url.getLongUrl(), shortUrl));
        return url.getLongUrl();
    }

    private void isExpired(Url url) {
        if (!(url.getDateTime().plusMinutes(lifeUrlMinutes).isAfter(ZonedDateTime.now()))) {
            log.info("Short URL %s expired".formatted(url.getShortUrl()));
            throw new LifeTimeExpiredException("Short URL %s expired"
                    .formatted(prefixUrl + url.getShortUrl()));
        }
    }

    private String getDecoderShortUrl(String longUrl) {
        try {
            MessageDigest messageDigest = MessageDigest.getInstance("SHA-256");
            byte[] hash = messageDigest.digest(longUrl.getBytes(StandardCharsets.UTF_8));
            return Base64.getUrlEncoder().withoutPadding().encodeToString(hash).substring(0, 8);
        } catch (NoSuchAlgorithmException ex) {
        log.warn("Failed to generate short URL %s"
                .formatted(prefixUrl + longUrl) + ": " + ex.getMessage());
        throw new GeneratedUrlException("Failed to generate short URL %s"
                .formatted(prefixUrl + longUrl) + ": " + ex.getMessage());
        }
    }

}
