package ru.ikusov.training.telegrambot.services;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;

public class HttpConnector {
    private final String urlString;

    public HttpConnector(String urlString) {
        this.urlString = urlString;
    }

    public String getJsonString() throws IOException {
        return getString("application/json; charset=utf-8");
    }

    public String getTextString() throws IOException {
        return getString("text/plain");
    }

    private String getString(String contentType) throws IOException {
        StringBuilder response = new StringBuilder();

        URL obj = new URL(urlString);
        HttpURLConnection connection = (HttpURLConnection) obj.openConnection();

        connection.setRequestMethod("GET");
        connection.setRequestProperty("User-Agent", "ZybarevBot/0.1");
        connection.setRequestProperty("Content-Type", contentType);
        connection.setConnectTimeout(10_000);
        connection.setReadTimeout(10_000);

        BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream(), StandardCharsets.UTF_8));
        String inputLine;

        while ((inputLine = in.readLine()) != null) {
            response.append(inputLine);
        }
        in.close();

        return response.toString();
    }
}
