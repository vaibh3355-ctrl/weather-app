# Weather Forecast Application

Spring Boot app that fetches live weather from the free [Open-Meteo](https://open-meteo.com) API
(no API key required) and shows temperature, humidity, wind and a 5-day forecast.

## Run

Requires Java 17+ and Maven 3.9+.

    mvn spring-boot:run

Open http://localhost:8080 or call the API directly:

    curl "http://localhost:8080/api/weather?city=Roorkee"
    curl "http://localhost:8080/api/weather?city=London&units=imperial"

## Test

    mvn test

## How it works

1. `WeatherController` validates `city` / `units` and exposes `GET /api/weather`.
2. `WeatherService` calls Open-Meteo's geocoding API (city -> lat/lon), then the forecast API.
3. The JSON is parsed with Jackson's `JsonNode` and mapped to the `WeatherReport` records.
4. `GlobalExceptionHandler` returns 404 (unknown city), 400 (bad input) and 502 (upstream down).
5. `static/index.html` is a small frontend that calls the API.

## Project layout

    src/main/java/com/example/weather
      WeatherApplication.java
      config/        RestClient beans + typed properties
      controller/    REST endpoint
      service/       API integration + JSON parsing
      model/         Response records, WMO code -> text
      exception/     Error handling
    src/main/resources
      application.properties
      static/index.html
