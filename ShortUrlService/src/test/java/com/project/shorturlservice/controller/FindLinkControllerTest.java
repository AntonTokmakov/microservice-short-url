package com.project.shorturlservice.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.transaction.annotation.Transactional;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@Transactional
@AutoConfigureMockMvc(printOnlyOnFailure = false)
class FindLinkControllerTest {

    @Autowired
    MockMvc mockMvc;

    @Test
    @Sql({"sql/data.sql"})
    void getLongLink__returnLongUrl() throws Exception {

        // given
        var requestExpired = MockMvcRequestBuilders.get("/find/long/VrgjTPgy");
        // when
        mockMvc.perform(requestExpired)
                // then
                .andExpectAll(
                        status().isGone(),
                        content().json("""
                                {
                                    "message":"Short URL http://localhost:8090/api/v1/VrgjTPgy expired",
                                    "status":"GONE"
                                }
                                """)
                );
    }

    @Test
    @Sql({"sql/data.sql"})
    void getLongLink_SuccessFindLongUrl_returnLongUrl() throws Exception {

        // given
        var requestExpired = MockMvcRequestBuilders.get("/find/long/eQwveHXA");
        // when
        mockMvc.perform(requestExpired)
                // then
                .andExpectAll(
                        status().isOk(),
                        content().json("""
                                {
                                    "longUrl": "https://www.gismeteo.ru/weather-novokuznetsk-4721/month/"
                                }
                                """)
                );
    }

}