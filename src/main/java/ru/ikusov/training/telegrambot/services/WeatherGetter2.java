package ru.ikusov.training.telegrambot.services;

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
import java.util.Map;

public class WeatherGetter2 {
    private final String url = "https://api.met.no/weatherapi/locationforecast/2.0/compact?lat=54.86&lon=83.09";
    private String forecast;
    private Map<Object, Object> weather;

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
            weather = parseWeather();

        } catch (IOException e) {
            throw new IOException("Не могу подключиться к сайту погоды " + url);
        } catch (ParseException e) {
            throw new IOException("Ошибка парсинга JSON по запросу " + url);
        } catch (Exception e) {
            throw new IOException(e.getMessage());
        }

    }

    private Map<Object, Object> parseWeather() throws ParseException {
        JSONObject wJson = (JSONObject) JSONValue.parseWithException(forecast);
        Map<Object, Object> weather = new HashMap<>((JSONObject) ((JSONObject) (((JSONObject) (((JSONObject) ((JSONArray) ((JSONObject) (wJson.get("properties"))).get("timeseries")).get(0)).get("data"))).get("instant"))).get("details"));
        weather.putAll((JSONObject)((JSONObject) (((JSONObject) (((JSONObject) ((JSONArray) ((JSONObject) (wJson.get("properties"))).get("timeseries")).get(0)).get("data"))).get("next_1_hours"))).get("summary"));
        weather.putAll((JSONObject)((JSONObject) (((JSONObject) (((JSONObject) ((JSONArray) ((JSONObject) (wJson.get("properties"))).get("timeseries")).get(0)).get("data"))).get("next_1_hours"))).get("details"));
        return weather;
    }

    public String getWeather() {
        String degrees = weather.get("air_temperature").toString(),
                description = weather.get("symbol_code").toString();

//        System.out.println("forecast = " + forecast);
        return String.format("По данным сайта %s, сейчас в Новосибирске %s, температура %s\u00b0 Цельсия.",
                                            url,                    description,        degrees);
    }
}
