package com.project.shorturlservice.service.impl;

import com.project.shorturlservice.exception.LifeTimeExpiredException;
import com.project.shorturlservice.exception.LinkNotFoundException;
import com.project.shorturlservice.exception.RedirectException;
import com.project.shorturlservice.model.Url;
import com.project.shorturlservice.repository.UrlRepository;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.util.ReflectionTestUtils;

import java.io.IOException;
import java.time.ZonedDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@SpringBootTest
@Transactional
class UrlServiceTest {

    @Mock
    UrlRepository urlRepository;

    @Mock
    HttpServletResponse response;

    @InjectMocks
    UrlService urlService;

    @Value("${app.url.prefix:http://localhost:8090/api/v1/}")
    String prefixUrl;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(urlService, "lifeUrlMinutes", 10);
        ReflectionTestUtils.setField(urlService, "prefixUrl", prefixUrl);
    }

    @Test
    void givenNewLongUrl_whenGenerateShortUrl_thenShortUrlGeneratedSuccessfully() {
        // given
        String longUrl = "https://www.gismeteo.ru/weather-novokuznetsk-4721/month/";
        String expectedShortUrl = prefixUrl + "eQwveHXA";

        when(urlRepository.existsByLongUrl(longUrl)).thenReturn(false);

        // when
        String shortUrl = urlService.generateShortUrl(longUrl);

        // then
        assertEquals(expectedShortUrl, shortUrl);
        verify(urlRepository, times(1)).existsByLongUrl(longUrl);
    }

    @Test
    void givenExistingLongUrl_whenGenerateShortUrl_thenUpdateShortUrlSuccessfully() {
        String longUrl = "https://do.sibsiu.ru/day/login/index.php";
        String shortUrl = "Mqmp91Rp";
        Url existingUrl = new Url(longUrl, shortUrl, ZonedDateTime.now().minusMinutes(5));

        when(urlRepository.existsByLongUrl(longUrl)).thenReturn(true);
        when(urlRepository.findByLongUrl(longUrl)).thenReturn(Optional.of(existingUrl));

        String resultUrl = urlService.generateShortUrl(longUrl);

        assertEquals(prefixUrl + shortUrl, resultUrl);
        verify(urlRepository, times(1)).save(any(Url.class));
        verify(urlRepository, times(1)).findByLongUrl(longUrl);
    }

    @Test
    void whenGenerateShortUrlWithNullInput_thenThrowException() {
        Assertions.assertThrows(IllegalArgumentException.class,
                () -> urlService.generateShortUrl(null));
    }

    @Test
    void whenDatabaseExceptionOnExistsBy_thenHandleGracefully() {
        String longUrl = "https://www.error.com";
        when(urlRepository.existsByLongUrl(longUrl)).thenThrow(new RuntimeException("Database error"));

        Exception exception = Assertions.assertThrows(RuntimeException.class,
                () -> urlService.generateShortUrl(longUrl));

        assertEquals("Database error", exception.getMessage());
    }

    @Test
    void whenRedirectTo_thenRedirectSuccessfully() throws IOException {
        String shortUrl = "Mqmp91Rp";
        String longUrl = "https://do.sibsiu.ru/day/login/index.php";
        Url url = new Url(longUrl, shortUrl, ZonedDateTime.now().plusHours(1));

        when(urlRepository.findByShortUrl(shortUrl)).thenReturn(Optional.of(url));

        urlService.redirectTo(shortUrl, response);

        verify(response).sendRedirect(longUrl);
    }

    @Test
    void whenRedirectTo_withNonExistentShortUrl_thenThrowLinkNotFoundException() {
        String shortUrl = "nonexistent";

        when(urlRepository.findByShortUrl(shortUrl)).thenReturn(Optional.empty());

        Assertions.assertThrows(LinkNotFoundException.class, () -> urlService.redirectTo(shortUrl, response));
    }

    @Test
    void whenRedirectTo_withExpiredUrl_thenThrowException() {
        String shortUrl = "Mqmp91Rp";
        String longUrl = "https://do.sibsiu.ru/day/login/index.php";
        Url url = new Url(longUrl, shortUrl, ZonedDateTime.now().minusMinutes(20));

        when(urlRepository.findByShortUrl(shortUrl)).thenReturn(Optional.of(url));

        Assertions.assertThrows(LifeTimeExpiredException.class, () -> urlService.redirectTo(shortUrl, response));
    }

    @Test
    void whenIOExceptionOnRedirect_thenHandleGracefully() throws IOException {
        String shortUrl = "Mqmp91Rp";
        String longUrl = "https://do.sibsiu.ru/day/login/index.php";
        Url url = new Url(longUrl, shortUrl, ZonedDateTime.now().plusHours(1));

        when(urlRepository.findByShortUrl(shortUrl)).thenReturn(Optional.of(url));
        doThrow(new IOException("Failed to send redirect")).when(response).sendRedirect(anyString());

        Assertions.assertThrows(RedirectException.class, () -> urlService.redirectTo(shortUrl, response));
    }

    @Test
    void getLongUrl_withValidShortUrl_thenReturnsLongUrl() {
        String shortUrl = "Mqmp91Rp";
        String longUrl = "https://do.sibsiu.ru/day/login/index.php";
        Url url = new Url(longUrl, shortUrl, ZonedDateTime.now().plusHours(1));

        when(urlRepository.findByShortUrl(shortUrl)).thenReturn(Optional.of(url));

        String resultUrl = urlService.getLongUrl(shortUrl);

        assertEquals(longUrl, resultUrl);
        verify(urlRepository).findByShortUrl(shortUrl);
    }

    @Test
    void whenGetLongUrl_withNonExistentShortUrl_thenThrowLinkNotFoundException() {
        String shortUrl = "nonexistent";

        when(urlRepository.findByShortUrl(shortUrl)).thenReturn(Optional.empty());

        Assertions.assertThrows(LinkNotFoundException.class, () -> urlService.getLongUrl(shortUrl));
    }

    @Test
    void whenGetLongUrl_withExpiredUrl_thenThrowException() {
        String shortUrl = "Mqmp91Rp";
        String longUrl = "https://do.sibsiu.ru/day/login/index.php";
        Url url = new Url(longUrl, shortUrl, ZonedDateTime.now().minusMinutes(10));

        when(urlRepository.findByShortUrl(shortUrl)).thenReturn(Optional.of(url));

        Assertions.assertThrows(LifeTimeExpiredException.class, () -> urlService.getLongUrl(shortUrl));
    }

    @Test
    void whenUrlExpired_thenThrowLifeTimeExpiredException() {
        String shortUrl = "Mqmp91Rp";
        String longUrl = "https://do.sibsiu.ru/day/login/index.php";
        Url expiredUrl = new Url(longUrl, shortUrl, ZonedDateTime.now().minusMinutes(10));

        when(urlRepository.findByShortUrl(shortUrl)).thenReturn(Optional.of(expiredUrl));

        Assertions.assertThrows(LifeTimeExpiredException.class, () -> urlService.getLongUrl(shortUrl));
    }


    @Test
    void whenGenerateShortUrlWithValidLongUrl_thenShortUrlIsGenerated() {
        String longUrl = "https://do.sibsiu.ru/day/login/index.php";
        String expectedShortUrl = "Mqmp91Rp";

        when(urlRepository.existsByLongUrl(longUrl)).thenReturn(false);
        when(urlRepository.save(any(Url.class))).thenAnswer(invocation -> invocation.getArgument(0));

        String resultUrl = urlService.generateShortUrl(longUrl);

        Assertions.assertTrue(resultUrl.contains(expectedShortUrl));
    }

}