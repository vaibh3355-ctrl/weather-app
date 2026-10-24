package com.example.weather;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.LocalDate;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.test.web.servlet.MockMvc;

import com.example.weather.controller.WeatherController;
import com.example.weather.exception.CityNotFoundException;
import com.example.weather.exception.GlobalExceptionHandler;
import com.example.weather.model.CurrentWeather;
import com.example.weather.model.DailyForecast;
import com.example.weather.model.WeatherReport;
import com.example.weather.service.WeatherService;

@WebMvcTest(WeatherController.class)
@Import(GlobalExceptionHandler.class)
class WeatherControllerTest {

    @Autowired MockMvc mvc;
    @MockBean WeatherService service;

    @Test
    void returnsWeatherForCity() throws Exception {
        var report = new WeatherReport("Roorkee", "India", "metric", "°C", "km/h",
                new CurrentWeather(28.5, 30.1, 62, 9.4, "Partly cloudy"),
                List.of(new DailyForecast(LocalDate.of(2026, 10, 24), 31, 18, 10, "Clear sky")));
        given(service.getWeather(anyString(), anyString())).willReturn(report);

        mvc.perform(get("/api/weather").param("city", "Roorkee"))
           .andExpect(status().isOk())
           .andExpect(jsonPath("$.city").value("Roorkee"))
           .andExpect(jsonPath("$.current.humidity").value(62))
           .andExpect(jsonPath("$.forecast[0].condition").value("Clear sky"));
    }

    @Test
    void returns404ForUnknownCity() throws Exception {
        given(service.getWeather(anyString(), anyString())).willThrow(new CityNotFoundException("Xyzzy"));

        mvc.perform(get("/api/weather").param("city", "Xyzzy"))
           .andExpect(status().isNotFound());
    }

    @Test
    void returns400WhenCityMissing() throws Exception {
        mvc.perform(get("/api/weather")).andExpect(status().isBadRequest());
    }
}
