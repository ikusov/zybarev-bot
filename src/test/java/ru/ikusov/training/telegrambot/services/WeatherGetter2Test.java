package ru.ikusov.training.telegrambot.services;

import junit.framework.TestCase;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import ru.ikusov.training.telegrambot.model.LocationEntity;

import java.io.IOException;

public class WeatherGetter2Test {
    WeatherGetter2 weatherGetter;

    @Before
    public void before() throws IOException {
        LocationEntity location = new LocationEntity().setAddress("Kukuego").setLatitude(54d).setLongitude(83d);
        weatherGetter = new WeatherGetter2(location);
    }

    @Test
    public void testGetWeather() {
        Assert.assertNotSame(weatherGetter.getWeather(), "");
    }

    @Test
    public void testGetWeatherForecast() {
        Assert.assertNotSame(weatherGetter.getWeatherForCurrent(), "");
    }

    @Test
    public void testGetWeatherForCurrent() {
        Assert.assertNotSame(weatherGetter.getWeatherForecast(), "");
    }
}