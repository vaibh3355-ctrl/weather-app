package com.example.weather.controller;

import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.weather.model.WeatherReport;
import com.example.weather.service.WeatherService;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@RestController
@RequestMapping("/api/weather")
@Validated
public class WeatherController {

    private final WeatherService service;

    public WeatherController(WeatherService service) {
        this.service = service;
    }

    /** GET /api/weather?city=Roorkee&units=metric */
    @GetMapping
    public WeatherReport weather(
            @RequestParam @NotBlank @Size(max = 100) String city,
            @RequestParam(defaultValue = "metric") String units) {
        return service.getWeather(city, units);
    }
}
