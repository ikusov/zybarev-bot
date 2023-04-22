package ru.ikusov.training.telegrambot.services;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.ikusov.training.telegrambot.MainClass;
import ru.ikusov.training.telegrambot.model.LocationEntity;

import java.io.IOException;

public class WeatherGetter {
    private static final Logger log = LoggerFactory.getLogger(WeatherGetter.class);
    private final String urlCurrent = "https://api.openweathermap.org/data/2.5/weather?units=metric&lang=ru";
    private final String urlForecast = "https://api.openweathermap.org/data/2.5/onecall?units=metric&lang=ru";

    private final double latitude;
    private final double longitude;
    private final String locationName;

    private final JsonNode weatherForecast;

    public WeatherGetter(LocationEntity location) throws IOException {

        this.locationName = location.getAddress();
        this.latitude = location.getLatitude();
        this.longitude = location.getLongitude();

        try {
            String urlForecastFull = formURLString(urlForecast);
            log.debug("Trying to get forecast from url '{}'", urlForecastFull);
            weatherForecast = parseWeather(new HttpConnector(urlForecastFull).getJsonString());
            log.debug("Forecast has been successfully getted!");
        } catch (JsonProcessingException e) {
            throw new IOException("Ошибка парсинга JSON с сайта погоды");
        } catch (IOException e) {
            throw new IOException("Не могу подключиться к сайту погоды");
        } catch (Exception e) {
            throw new IOException(e.getMessage());
        }
    }

    public String getWeather() {
        log.debug("Parsing forecast for current and tomorrow...");
        JsonNode currentWeatherForecast = weatherForecast.get("current");
        JsonNode tomorrowWeatherForecast = weatherForecast.get("daily").get(0);
        log.debug("Parsed successfully!" +
                " Getting description for current...");
        String description = currentWeatherForecast.get("weather").get(0).get("description").asText();
        Double temp = currentWeatherForecast.get("temp").asDouble();
        log.debug("Successfully get description: '{}', temperature: '{}'", description, temp);

        log.debug("Parsing description for forecast...");
        String descriptionForecast = tomorrowWeatherForecast.get("weather").get(0).get("description").asText();
        Integer probabilityOfPrecipitation = (int) (tomorrowWeatherForecast.get("pop").asDouble() * 100);
        Double tempNightForecast = tomorrowWeatherForecast.get("temp").get("night").asDouble();
        Double tempDayForecast = tomorrowWeatherForecast.get("temp").get("day").asDouble();
        log.debug("Successfully parsed '{}', night temp '{}'," +
                        " day temp '{}', percipitation probability '{}'",
                descriptionForecast, tempNightForecast, tempDayForecast, probabilityOfPrecipitation);

        return String.format("%s: %s, температура %.1f°C.\n" +
                        "В ближайшие сутки ожидается %s, вероятность осадков %d%%, температура ночью %.1f°C, днём %.1f\u00b0C.",
                locationName, description, temp, descriptionForecast, probabilityOfPrecipitation, tempNightForecast, tempDayForecast);
    }

    private JsonNode parseWeather(String weatherString) throws JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();
        return mapper.readTree(weatherString);
    }

    private String formURLString(String url) {
        return url + "&appid=" + MainClass.WEATHER_API_KEY + "&lat=" + latitude + "&lon=" + longitude;
    }
}
