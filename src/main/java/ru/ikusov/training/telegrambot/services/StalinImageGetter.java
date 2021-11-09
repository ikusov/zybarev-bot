package ru.ikusov.training.telegrambot.services;

import org.jsoup.Jsoup;
import org.jsoup.select.Selector;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

import static ru.ikusov.training.telegrambot.utils.MyMath.r;

@Component
public class StalinImageGetter {
    private final String URL = "https://yandex.ru/images/search?text=%D1%81%D1%82%D0%B0%D0%BB%D0%B8%D0%BD";
    private List<String> stalinImagesList;

    public StalinImageGetter() throws IOException {
        try {
            stalinImagesList = Jsoup.connect(URL)
                    .get().select("img").stream()
                    .map(img -> img.attr("abs:src"))
                    .collect(Collectors.toList());
        } catch (IOException e) {
            throw new IOException("Не могу подключиться к сайту " + URL);
        } catch (Selector.SelectorParseException e) {
            throw new IOException("Невалидный CSS запрос в методе парсинга сайта " + URL);
        }
    }

    public String getStalinImage() {
        if (stalinImagesList != null && !stalinImagesList.isEmpty()) {
            int index = r(stalinImagesList.size());
            return stalinImagesList.get(index);
        } else {
            throw new NoSuchElementException("Изображения великого вождя временно запрещены к пересылке!");
        }
    }
}
