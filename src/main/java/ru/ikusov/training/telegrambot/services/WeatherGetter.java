package ru.ikusov.training.telegrambot.services;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.Properties;

public class WeatherGetter {
    private final String url = "https://pogoda.ngs.ru/";
    private Document document;

    public WeatherGetter() throws IOException {
        try {
            document = Jsoup.connect(url).get();
        } catch (IOException e) {
            throw new IOException("Не могу подключиться к сайту погоды " + url);
        }
    }

    public String getWeather() {
        String degrees = document.select("span.value__main").text(),
                description = document.select("span.value-description").text();

        return String.format("По данным сайта %s, сейчас в Новосибирске %s, температура %s\u00b0 Цельсия.",
                                            url,                    description,        degrees);
    }
}
