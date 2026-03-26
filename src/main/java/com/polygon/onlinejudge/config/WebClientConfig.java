package com.polygon.onlinejudge.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class WebClientConfig {

    @Bean
    public WebClient judge0WebClient() {
        return WebClient.builder()
                .baseUrl("http://100.68.203.47:2358")
                .build();
    }
}