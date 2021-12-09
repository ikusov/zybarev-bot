package ru.ikusov.training.telegrambot.services;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;
import org.json.simple.parser.ParseException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class WeatherGetter2 {
    private final String url = "https://api.met.no/weatherapi/locationforecast/2.0/compact?lat=54.86&lon=83.09";
    private String forecast;
    private JsonNode weatherForecast;

    public WeatherGetter2() throws IOException {
        StringBuffer response = new StringBuffer();

        try {
            URL obj = new URL(url);
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
            throw new IOException("Не могу подключиться к сайту погоды " + url);
        } catch (ParseException e) {
            throw new IOException("Ошибка парсинга JSON по запросу " + url);
        } catch (Exception e) {
            throw new IOException(e.getMessage());
        }

    }

    private JsonNode parseWeather() throws ParseException, JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();
        JsonNode wJsonNode = mapper.readTree(forecast);
        return wJsonNode.get("properties").get("timeseries");
    }

    public String getWeather() {
        String degreesNow = weatherForecast.get(0).get("data").get("instant").get("details").get("air_temperature").asText(),
                descriptionNow = weatherForecast.get(0).get("data").get("next_1_hours").get("summary").get("symbol_code").asText(),
                degreesTomorrow = weatherForecast.get(24).get("data").get("instant").get("details").get("air_temperature").asText(),
                descriptionTomorrow = weatherForecast.get(24).get("data").get("next_1_hours").get("summary").get("symbol_code").asText();
//        System.out.println("forecast = " + forecast);
        return String.format("Если верить норвежцам, сейчас в Новосибирске %s, температура %s\u00b0 Цельсия.\n" +
                                "В ближайшие сутки будет %s, температура %s\u00b0 Цельсия.",
                                descriptionNow, degreesNow, descriptionTomorrow, degreesTomorrow);
    }
}
