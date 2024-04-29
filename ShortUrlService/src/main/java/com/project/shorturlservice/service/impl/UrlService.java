package com.project.shorturlservice.service.impl;

import com.project.shorturlservice.exception.*;
import com.project.shorturlservice.model.Url;
import com.project.shorturlservice.repository.UrlRepository;
import com.project.shorturlservice.service.FindLink;
import com.project.shorturlservice.service.GeneratorService;
import com.project.shorturlservice.service.RedirectService;
import com.project.shorturlservice.service.ScheduledRemoveExpired;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.ZonedDateTime;
import java.util.Base64;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class UrlService implements GeneratorService, RedirectService, FindLink, ScheduledRemoveExpired {

    private final UrlRepository urlRepository;

    @Value("${life.url.minutes:10}")
    private int lifeUrlMinutes;

    @Value("${app.url.prefix:http://localhost:8090/api/v1/}")
    private String prefixUrl;

    @Override
    public String getShortUrl(String longUrl) {
        Url url = urlRepository.findByLongUrl(longUrl)
                .orElseThrow(() -> new LinkNotFoundException("Short URL %s not found"
                        .formatted( longUrl)));
        log.info("Get Short URL: %s long URL: %s".formatted(url.getShortUrl(), longUrl));
        return  prefixUrl + url.getShortUrl();
    }

    @Override
    public String generateShortUrl(String longUrl) {
        if (urlRepository.existsByLongUrl(longUrl)) {
            throw new ExistsLinkException("Long URL %s already exists. Use GET /find?longUrl=%s"
                    .formatted(longUrl, longUrl));
        }

        try {
            MessageDigest messageDigest = MessageDigest.getInstance("SHA-256");
            byte[] hash = messageDigest.digest(longUrl.getBytes(StandardCharsets.UTF_8));
            String shortUrl = Base64.getUrlEncoder().withoutPadding().encodeToString(hash).substring(0, 8);
            urlRepository.save(new Url(longUrl, shortUrl, ZonedDateTime.now()));
            log.info("Create Short URL: %s long URL: %s".formatted(prefixUrl +shortUrl,  longUrl));
            return prefixUrl + shortUrl;
        } catch (NoSuchAlgorithmException ex) {
            log.warn("Failed to generate short URL %s"
                    .formatted(prefixUrl + longUrl) + ": " + ex.getMessage());
            throw new GeneratedUrlException("Failed to generate short URL %s"
                    .formatted(prefixUrl + longUrl) + ": " + ex.getMessage());
        }
    }

    @Override
    public String getLongUrl(String shortUrl) {
        Url url = urlRepository.findByShortUrl(shortUrl)
                .orElseThrow(() -> new LinkNotFoundException("Short URL %s not found"
                        .formatted(shortUrl)));
        log.info("Get Long URL: %s short URL: %s".formatted(url.getLongUrl(), shortUrl));
        return url.getLongUrl();
    }

    @Override
    public void redirectTo(String shortUrl, HttpServletResponse response) {

        Url url = urlRepository.findByShortUrl(shortUrl)
                .orElseThrow(() -> new LinkNotFoundException("Short URL %s not found"
                        .formatted(prefixUrl + shortUrl)));

        if (!(url.getDateTime().plusMinutes(lifeUrlMinutes).isAfter(ZonedDateTime.now()))) {
            log.info("Short URL %s expired".formatted(shortUrl));
            throw new LifeTimeExpiredException("Short URL %s expired"
                    .formatted(prefixUrl + shortUrl));
        }

        try {
            response.sendRedirect(url.getLongUrl());
        } catch (IOException e) {
            log.warn("Failed to redirect to %s".formatted(url.getLongUrl()) + ": " + e.getMessage());
            throw new RedirectException("Failed to redirect to %s"
                    .formatted(prefixUrl + url.getLongUrl()));
        }
    }

    @Override
    @Scheduled(fixedRate = 60 * 60 * 1000)
    @Transactional()
    public void removeExpiredUrls() {
        List<Url> expiredUrls = urlRepository
                .findAllByDateTimeBefore(ZonedDateTime.now().minusMinutes(lifeUrlMinutes));
        if (!expiredUrls.isEmpty()) {
            log.info("Remove expired URLs: %s".formatted(expiredUrls.stream()
                    .map(Url::getShortUrl)
                    .collect(Collectors.toList())));
            urlRepository.deleteAll(expiredUrls);
        }
    }
}
