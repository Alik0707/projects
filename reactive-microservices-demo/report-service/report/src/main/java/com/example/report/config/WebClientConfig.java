package com.example.report.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.Base64;

@Configuration
public class WebClientConfig {

    @Bean
    public WebClient productWebClient() {
        return WebClient.builder()
                .baseUrl("http://localhost:8080")   // порт Product-сервиса
                .defaultHeader(HttpHeaders.AUTHORIZATION, "Basic " + base64("admin:admin"))
                .build();
    }

    private String base64(String s) {
        return Base64.getEncoder().encodeToString(s.getBytes());
    }
}