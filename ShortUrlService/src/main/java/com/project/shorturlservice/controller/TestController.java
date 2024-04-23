package com.project.shorturlservice.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("api/v1/")
public class TestController {

    @Value("${eureka.instance.instance-id}")
    private String id;
    @PostMapping("/generate")
    public String test() {
        System.out.println("Hello from " + id.substring(2, id.length() - 1));
        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        return id;
    }

}
