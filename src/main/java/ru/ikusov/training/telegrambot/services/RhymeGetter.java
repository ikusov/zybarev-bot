package ru.ikusov.training.telegrambot.services;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.jsoup.select.Selector;

import java.io.IOException;
import java.util.NoSuchElementException;

import static ru.ikusov.training.telegrambot.utils.MyMath.r;

public class RhymeGetter {
    private final String[] urls = {"https://www.anekdotovmir.ru/smeshnye-stishki-poroshki/",
            "https://www.anekdotovmir.ru/smeshnye-stishki-poroshki/2/",
            "https://www.anekdotovmir.ru/smeshnye-stishki-poroshki/3/",
            "https://www.anekdotovmir.ru/smeshnye-stishki-poroshki/4/",
            "https://www.anekdotovmir.ru/smeshnye-stishki-poroshki/5/",
            "https://www.anekdotovmir.ru/smeshnye-stishki-poroshki/6/",
            "https://www.anekdotovmir.ru/smeshnye-stishki-poroshki/7/",
                                    "https://www.anekdotovmir.ru/smeshnye-stishki-pirozhki/",
            "https://www.anekdotovmir.ru/smeshnye-stishki-pirozhki/2/",
            "https://www.anekdotovmir.ru/smeshnye-stishki-pirozhki/3/",
            "https://www.anekdotovmir.ru/smeshnye-stishki-pirozhki/4/",
            "https://www.anekdotovmir.ru/smeshnye-stishki-pirozhki/5/"
            };
    private final Document document;
    private final String text;

    public RhymeGetter() throws IOException {
        String url = urls[r(urls.length)];
        try {
            document = Jsoup.connect(url).get();
            Elements elements = document.select("div#content p");
            if (elements.isEmpty())
                throw new NoSuchElementException("Не найден контент со стишками на сайте анекдотцов " + url);
            text = elements.get(r(elements.size()-1)).text();
        } catch (IOException e) {
            throw new IOException("Не могу подключиться к сайту анекдотцов " + url);
        } catch (Selector.SelectorParseException e) {
            throw new IOException("Невалидный CSS запрос в методе парсинга сайта анекдотцов " + url);
        }
    }

    public String getRhyme() {
        return text;
    }
}
