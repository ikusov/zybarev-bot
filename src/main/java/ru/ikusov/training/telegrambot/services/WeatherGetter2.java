package ru.ikusov.training.telegrambot.services;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;
import org.json.simple.parser.ParseException;
import ru.ikusov.training.telegrambot.model.LocationEntity;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class WeatherGetter2 {
    private final String url = "https://api.openweathermap.org/data/2.5/weather?units=metric&lang=ru";
    private String forecast;
    private JsonNode weatherForecast;

    public WeatherGetter2(LocationEntity location) throws IOException {
        StringBuffer response = new StringBuffer();

        String apiKey = System.getenv("weather_api_token");
        double lat = location.getLatitude(),
                lon = location.getLongitude();

        try {
            URL obj = new URL(url + "&appid=" + apiKey + "&lat=" + lat + "&lon=" + lon);
            HttpURLConnection connection = (HttpURLConnection) obj.openConnection();

            connection.setRequestMethod("GET");
            connection.setRequestProperty("User-Agent", "ZybarevBot/0.1");

            BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            String inputLine;

            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }
            in.close();
            forecast = response.toString();
            weatherForecast = parseWeather();

        } catch (IOException e) {
            throw new IOException("Не могу подключиться к сайту погоды");
        } catch (ParseException e) {
            throw new IOException("Ошибка парсинга JSON по запросу");
        } catch (Exception e) {
            throw new IOException(e.getMessage());
        }

    }

    private JsonNode parseWeather() throws ParseException, JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();
        return mapper.readTree(forecast);
    }

    public String getWeather() {
        String location = weatherForecast.get("name").asText();
        String description = weatherForecast.get("weather").get(0).get("description").asText();
        Double temp = weatherForecast.get("main").get("temp").asDouble();

        return String.format("%s: %s, температура %.1f\u00b0C",
                                location, description, temp);
    }
}
