package com.example.weather.service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import com.example.weather.exception.CityNotFoundException;
import com.example.weather.model.CurrentWeather;
import com.example.weather.model.DailyForecast;
import com.example.weather.model.WeatherCode;
import com.example.weather.model.WeatherReport;
import com.fasterxml.jackson.databind.JsonNode;

@Service
public class WeatherService {

    private static final int FORECAST_DAYS = 5;

    private final RestClient geocodingClient;
    private final RestClient forecastClient;

    public WeatherService(RestClient geocodingClient, RestClient forecastClient) {
        this.geocodingClient = geocodingClient;
        this.forecastClient = forecastClient;
    }

    public WeatherReport getWeather(String city, String units) {
        boolean imperial = "imperial".equalsIgnoreCase(units);

        // Step 1: city name -> coordinates
        JsonNode place = geocode(city);

        // Step 2: coordinates -> current conditions + daily forecast
        JsonNode root = forecastClient.get()
                .uri(uri -> uri.path("/v1/forecast")
                        .queryParam("latitude", place.path("latitude").asDouble())
                        .queryParam("longitude", place.path("longitude").asDouble())
                        .queryParam("current",
                                "temperature_2m,relative_humidity_2m,apparent_temperature,wind_speed_10m,weather_code")
                        .queryParam("daily",
                                "weather_code,temperature_2m_max,temperature_2m_min,precipitation_probability_max")
                        .queryParam("forecast_days", FORECAST_DAYS)
                        .queryParam("timezone", "auto")
                        .queryParam("temperature_unit", imperial ? "fahrenheit" : "celsius")
                        .queryParam("wind_speed_unit", imperial ? "mph" : "kmh")
                        .build())
                .retrieve()
                .body(JsonNode.class);

        return toReport(place, root, imperial);
    }

    private JsonNode geocode(String city) {
        JsonNode result = geocodingClient.get()
                .uri(uri -> uri.path("/v1/search")
                        .queryParam("name", city.trim())
                        .queryParam("count", 1)
                        .queryParam("language", "en")
                        .build())
                .retrieve()
                .body(JsonNode.class);

        JsonNode results = result == null ? null : result.path("results");
        if (results == null || !results.isArray() || results.isEmpty()) {
            throw new CityNotFoundException(city);
        }
        return results.get(0);
    }

    private WeatherReport toReport(JsonNode place, JsonNode root, boolean imperial) {
        JsonNode cur = root.path("current");
        CurrentWeather current = new CurrentWeather(
                cur.path("temperature_2m").asDouble(),
                cur.path("apparent_temperature").asDouble(),
                cur.path("relative_humidity_2m").asInt(),
                cur.path("wind_speed_10m").asDouble(),
                WeatherCode.describe(cur.path("weather_code").asInt(-1)));

        JsonNode daily = root.path("daily");
        JsonNode dates = daily.path("time");
        List<DailyForecast> forecast = new ArrayList<>();
        for (int i = 0; i < dates.size(); i++) {
            forecast.add(new DailyForecast(
                    LocalDate.parse(dates.get(i).asText()),
                    daily.path("temperature_2m_max").path(i).asDouble(),
                    daily.path("temperature_2m_min").path(i).asDouble(),
                    daily.path("precipitation_probability_max").path(i).asInt(),
                    WeatherCode.describe(daily.path("weather_code").path(i).asInt(-1))));
        }

        return new WeatherReport(
                place.path("name").asText(),
                place.path("country").asText(""),
                imperial ? "imperial" : "metric",
                imperial ? "°F" : "°C",
                imperial ? "mph" : "km/h",
                current,
                forecast);
    }
}
