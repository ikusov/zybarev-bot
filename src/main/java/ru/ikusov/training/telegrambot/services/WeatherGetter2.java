package ru.ikusov.training.telegrambot.services;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import ru.ikusov.training.telegrambot.MainClass;
import ru.ikusov.training.telegrambot.model.LocationEntity;

import java.io.IOException;

public class WeatherGetter2 {
    private final String urlCurrent = "https://api.openweathermap.org/data/2.5/weather?units=metric&lang=ru";
    private final String urlForecast = "https://api.openweathermap.org/data/2.5/onecall?units=metric&lang=ru";

    private final double latitude;
    private final double longitude;
    private final String locationName;

    private final JsonNode weatherForecast;
    private final JsonNode weatherCurrent;

    public WeatherGetter2(LocationEntity location) throws IOException {

        this.locationName = location.getAddress();
        this.latitude = location.getLatitude();
        this.longitude = location.getLongitude();

        try {
            String urlCurrentFull = formURLString(urlCurrent);
            String urlForecastFull = formURLString(urlForecast);
            weatherCurrent = parseWeather(new HttpConnector(urlCurrentFull).getJsonString());
            weatherForecast = parseWeather(new HttpConnector(urlForecastFull).getJsonString());
        } catch (JsonProcessingException e) {
            throw new IOException("Ошибка парсинга JSON с сайта погоды");
        } catch (IOException e) {
            throw new IOException("Не могу подключиться к сайту погоды");
        } catch (Exception e) {
            throw new IOException(e.getMessage());
        }

    }

    private String formURLString(String url) {
        return url + "&appid=" + MainClass.WEATHER_API_KEY + "&lat=" + latitude + "&lon=" + longitude;
    }

    private JsonNode parseWeather(String weatherString) throws JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();
        return mapper.readTree(weatherString);
    }

    public String getWeather() {
        return getWeatherForecast();
    }

    public String getWeatherForecast() {
        JsonNode currentWeatherForecast = weatherForecast.get("current");
        JsonNode tomorrowWeatherForecast = weatherForecast.get("daily").get(0);
        String description = currentWeatherForecast.get("weather").get(0).get("description").asText();
        Double temp = currentWeatherForecast.get("temp").asDouble();

        String descriptionForecast = tomorrowWeatherForecast.get("weather").get(0).get("description").asText();
        Integer probabilityOfPrecipitation = (int)(tomorrowWeatherForecast.get("pop").asDouble()*100);
        Double tempNightForecast = tomorrowWeatherForecast.get("temp").get("night").asDouble();
        Double tempDayForecast = tomorrowWeatherForecast.get("temp").get("day").asDouble();

        return String.format("%s: %s, температура %.1f\u00b0C.\n" +
                        "В ближайшие сутки ожидается %s, вероятность осадков %d%%, температура ночью %.1f\u00b0C, днём %.1f\u00b0C.",
                locationName, description, temp, descriptionForecast, probabilityOfPrecipitation, tempNightForecast, tempDayForecast);
    }

    public String getWeatherForCurrent() {
        String description = weatherCurrent.get("weather").get(0).get("description").asText();
        Double temp = weatherCurrent.get("main").get("temp").asDouble();

        return String.format("%s: %s, температура %.1f\u00b0C",
                locationName, description, temp);
    }
}
