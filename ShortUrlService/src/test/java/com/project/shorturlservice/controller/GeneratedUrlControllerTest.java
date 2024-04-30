package com.project.shorturlservice.controller;

import jakarta.transaction.Transactional;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@Transactional
@AutoConfigureMockMvc(printOnlyOnFailure = false)
class GeneratedUrlControllerTest {


    @Autowired
    MockMvc mockMvc;

    @Test
    void generateShortUrl_SuccessCreateShortUrl_returnShortUrl() throws Exception {

        // given
        MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders.post("/generate")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                        {"longUrl":"https://www.gismeteo.ru/weather-novokuznetsk-4721/3-days/"}
                        """);
        // when
        this.mockMvc.perform(requestBuilder)
                // then
                .andExpectAll(
                        status().isOk(),
                        content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON),
                        content().json(
                                "{\"shortUrl\":\"http://localhost:8090/api/v1/UZC3aL6Q\"}"
                        )
                );

    }
}