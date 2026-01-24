package com.polygon.onlinejudge.controllers;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController("/test-connection")
public class BasicController {

    @GetMapping()
    public String test() {
        return "Connected to the API";
    }
}
