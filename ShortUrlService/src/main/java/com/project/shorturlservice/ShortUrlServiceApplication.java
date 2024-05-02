package com.project.shorturlservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@SpringBootApplication
@EnableDiscoveryClient
public class ShortUrlServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(ShortUrlServiceApplication.class, args);
    }

}
