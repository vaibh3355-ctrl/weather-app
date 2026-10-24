package com.example.weather.model;

import java.time.LocalDate;

public record DailyForecast(
        LocalDate date,
        double tempMax,
        double tempMin,
        int precipitationChance,
        String condition) {}
