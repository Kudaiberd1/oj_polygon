package com.polygon.onlinejudge.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.ExchangeStrategies;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class WebClientConfig {

    @Value("${judge0.base-url}")
    private String judge0BaseUrl;

    @Bean
    public WebClient judge0WebClient() {
        return WebClient.builder()
                .baseUrl(judge0BaseUrl)
                .exchangeStrategies(ExchangeStrategies.builder()
                        .codecs(cfg -> cfg.defaultCodecs().maxInMemorySize(10 * 1024 * 1024))
                        .build())
                .build();
    }
}