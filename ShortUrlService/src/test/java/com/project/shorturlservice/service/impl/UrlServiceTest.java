package com.project.shorturlservice.service.impl;

import com.project.shorturlservice.repository.UrlRepository;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.util.ReflectionTestUtils;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@SpringBootTest
@Transactional
class UrlServiceTest {

    @Mock
    UrlRepository urlRepository;

    @InjectMocks
    UrlService urlService;

    @Value("${app.url.prefix:http://localhost:8090/api/v1/}")
    String prefixUrl;

//    @Test
//    void generateShortUrlSuccess() {
//        String longUrl = "https://www.gismeteo.ru/weather-novokuznetsk-4721/month/";
//        String shortUrl = "nulleQwveHXA";
//        String result = urlService.generateShortUrl(longUrl);
//        assertThat(result).isEqualTo(shortUrl);
//    }
//
//    @Test
//    void generateShortUrl_SuccessCreateShortUrl_returnShortUrl() throws Exception {
//        // Arrange
//        String longUrl = "https://www.example.com/long-url";
//        when(urlRepository.existsByLongUrl(longUrl)).thenReturn(true);
//
//        // Act and Assert
//        assertThrows(ExistsLinkException.class, () -> urlService.generateShortUrl(longUrl));
//        verify(urlRepository, never()).save(any(Url.class));
//    }

//    @Test
//    void getShortUrl_SuccessCreateShortUrl_returnShortUrl() throws Exception {
//        GeneratedUrlRequest request =
//                new GeneratedUrlRequest("https://www.gismeteo.ru/weather-novokuznetsk-4721/month/");
//        GeneratedUrlResponse response = new GeneratedUrlResponse("http://localhost:8090/api/v1/eQwveHXA");
//
//        // Настраиваем поведение мока
//        Mockito.when(generatorService
//                        .generateShortUrl("https://www.gismeteo.ru/weather-novokuznetsk-4721/month/"))
//                .thenReturn("http://localhost:8090/api/v1/eQwveHXA");

        // Формируем JSON запроса
//        String requestJson = new ObjectMapper().writeValueAsString(request);
//
//        // Выполняем POST запрос и проверяем ответ
//        mockMvc.perform(post("/generate")
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .content(requestJson))
//                .andExpect(status().isCreated())
//                .andExpect(jsonPath("$.shortUrl").value("http://localhost:8090/api/v1/eQwveHXA"));
//    }

    @Test
    void givenNewLongUrl_whenGenerateShortUrl_thenShortUrlGeneratedSuccessfully() throws NoSuchFieldException, IllegalAccessException {
        ReflectionTestUtils.setField(urlService, "prefixUrl", prefixUrl);
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
    void getShortUrl() {



    }

    @Test
    void getLongUrl() {
    }

    @Test
    void redirectTo() {



    }

    @Test
    void removeExpiredUrls() {
    }
}