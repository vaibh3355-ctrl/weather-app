package com.example.weather.model;

public record CurrentWeather(
        double temperature,
        double feelsLike,
        int humidity,
        double windSpeed,
        String condition) {}
