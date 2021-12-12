package ru.ikusov.training.telegrambot.services;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import ru.ikusov.training.telegrambot.model.LocationEntity;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Locale;

/**
 * Class for getting geocode by location name using Yandex Geocode API
 */
public final class GeocodeGetter {
    private final String geoCode;
    private final String urlString;
    private final String apiKey;

    public GeocodeGetter(String geoCode) {
        String urlSting = "https://geocode-maps.yandex.ru/1.x?format=json";
        String geoCodeURLenc = URLEncoder.encode(geoCode, StandardCharsets.UTF_8);
        this.geoCode = geoCode;
        this.apiKey = System.getenv("geocode_api_key");
        this.urlString = urlSting + "&apikey=" + apiKey + "&geocode=" + geoCodeURLenc;
    }

    public LocationEntity getGeoCode() throws IOException {
        String jsonString = new HttpConnector(urlString).getJsonString();

        ObjectMapper mapper = new ObjectMapper();
        JsonNode code = mapper.readTree(jsonString)
                .get("response")
                .get("GeoObjectCollection")
                .get("featureMember")
                .get(0)
                .get("GeoObject");

        String address = code.get("name").asText(),
                pos = code.get("Point").get("pos").asText();

        double lon = Double.parseDouble(pos.split(" ")[0]),
                lat = Double.parseDouble(pos.split(" ")[1]);

        LocationEntity location = new LocationEntity()
                .setAddress(address)
                .setAliases(geoCode + ";" + address.toLowerCase(Locale.ROOT))
                .setLongitude(lon)
                .setLatitude(lat);

        return location;
    }
}
