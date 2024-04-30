package com.project.shorturlservice.controller;

import jakarta.transaction.Transactional;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


@SpringBootTest
@Transactional
@AutoConfigureMockMvc(printOnlyOnFailure = false)
class RedirectControllerTest {

    @Autowired
    MockMvc mockMvc;

    @Test
    @Sql({"sql/data.sql"})
    void redirect_SuccessRedirectUrl_returnSite() throws Exception {

        // given
        var requestRedirect = MockMvcRequestBuilders.get("/eQwveHXA");
        // when
        mockMvc.perform(requestRedirect)
        // then
                .andExpectAll(
                        status().isFound(),
                        redirectedUrl("https://www.gismeteo.ru/weather-novokuznetsk-4721/month/")
                );
    }

    @Test
    void redirect_FailRedirectUrl_returnNotFoundException() throws Exception {

        // given
        var requestRedirect = MockMvcRequestBuilders.get("/eQwveHXA");
        // when
        mockMvc.perform(requestRedirect)
        // then
                .andExpectAll(
                        status().isNotFound()
                );
    }

    @Test
    @Sql({"sql/data.sql"})
    void redirect_FailRedirectionTimeout_returnExpiredException() throws Exception {
        // given
        var requestExpired = MockMvcRequestBuilders.get("/VrgjTPgy");
        // when
        mockMvc.perform(requestExpired)
        // then
                .andExpectAll(
                        status().isGone(),
                        content().json("""
                    {
                        "message":"Short URL http://localhost:8090/api/v1/VrgjTPgy expired"
                    }
                    """)
                );
    }
}