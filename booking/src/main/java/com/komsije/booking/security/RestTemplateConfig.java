package com.komsije.booking.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RestTemplateConfig {
    @Bean
    public org.springframework.web.client.RestTemplate restTemplate(){
        return new org.springframework.web.client.RestTemplate();
    }

}
