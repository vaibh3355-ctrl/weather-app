package com.example.weather.exception;

public class CityNotFoundException extends RuntimeException {
    public CityNotFoundException(String city) {
        super("No city found matching \"" + city + "\". Check the spelling or try a nearby city.");
    }
}
