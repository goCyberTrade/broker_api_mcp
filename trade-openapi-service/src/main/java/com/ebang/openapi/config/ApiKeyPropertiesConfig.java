package com.ebang.openapi.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.Map;

@Configuration
@ConfigurationProperties
@Data
public class ApiKeyPropertiesConfig {

    private Map<String, String> apiKey;
}