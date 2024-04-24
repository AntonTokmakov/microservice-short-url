package com.project.shorturlservice.controller.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class GeneratedUrlResponse {

        private String shortUrl;

        public GeneratedUrlResponse(String shortUrl) {

                this.shortUrl = "http://localhost:8090/api/v1/" + shortUrl;
        }
}
