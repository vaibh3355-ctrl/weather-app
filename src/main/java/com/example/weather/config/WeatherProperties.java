package com.example.weather.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "weather")
public record WeatherProperties(String geocodingUrl, String forecastUrl, int timeoutSeconds) {}
