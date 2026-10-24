package com.example.weather.model;

import java.util.List;

public record WeatherReport(
        String city,
        String country,
        String units,
        String temperatureUnit,
        String windUnit,
        CurrentWeather current,
        List<DailyForecast> forecast) {}
