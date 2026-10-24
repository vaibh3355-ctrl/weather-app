package com.example.weather.config;

import java.time.Duration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.web.client.RestClient;

@Configuration
public class RestClientConfig {

    private static SimpleClientHttpRequestFactory factory(int timeoutSeconds) {
        var f = new SimpleClientHttpRequestFactory();
        f.setConnectTimeout(Duration.ofSeconds(timeoutSeconds));
        f.setReadTimeout(Duration.ofSeconds(timeoutSeconds));
        return f;
    }

    @Bean
    public RestClient geocodingClient(WeatherProperties props) {
        return RestClient.builder()
                .baseUrl(props.geocodingUrl())
                .requestFactory(factory(props.timeoutSeconds()))
                .build();
    }

    @Bean
    public RestClient forecastClient(WeatherProperties props) {
        return RestClient.builder()
                .baseUrl(props.forecastUrl())
                .requestFactory(factory(props.timeoutSeconds()))
                .build();
    }
}
