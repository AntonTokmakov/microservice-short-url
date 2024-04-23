package com.project.shorturlserver;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.server.EnableEurekaServer;

@SpringBootApplication
@EnableEurekaServer
public class ShortUrlServerApplication {

    public static void main(String[] args) {
        SpringApplication.run(ShortUrlServerApplication.class, args);
    }

}
