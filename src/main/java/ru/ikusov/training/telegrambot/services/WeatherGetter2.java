package ru.ikusov.training.telegrambot.services;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import ru.ikusov.training.telegrambot.model.LocationEntity;

import java.io.IOException;

public class WeatherGetter2 {
    private final String url = "https://api.openweathermap.org/data/2.5/weather?units=metric&lang=ru";
    private String locationName;

    private String forecast;
    private JsonNode weatherForecast;

    public WeatherGetter2(LocationEntity location) throws IOException {

        locationName = location.getAddress();
        String apiKey = System.getenv("weather_api_token");
        double lat = location.getLatitude(),
                lon = location.getLongitude();

        String urlString = url + "&appid=" + apiKey + "&lat=" + lat + "&lon=" + lon;

        try {
            forecast = new HttpConnector(urlString).getJsonString();
            weatherForecast = parseWeather();

        } catch (JsonProcessingException e) {
            throw new IOException("Ошибка парсинга JSON по запросу");
        } catch (IOException e) {
            throw new IOException("Не могу подключиться к сайту погоды");
        } catch (Exception e) {
            throw new IOException(e.getMessage());
        }

    }

    private JsonNode parseWeather() throws JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();
        return mapper.readTree(forecast);
    }

    public String getWeather() {
        String location = locationName;
        String description = weatherForecast.get("weather").get(0).get("description").asText();
        Double temp = weatherForecast.get("main").get("temp").asDouble();

        return String.format("%s: %s, температура %.1f\u00b0C",
                                location, description, temp);
    }
}
