package ru.ikusov.training.telegrambot.services;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.jsoup.select.Selector;
import org.telegram.telegrambots.meta.api.objects.stickers.Sticker;

import java.io.IOException;
import java.util.NoSuchElementException;

import static ru.ikusov.training.telegrambot.utils.MyMath.r;

public class NewsGetter {
    private final String url = "https://yandex.ru/news/?lang=ru";
    private final Document document;
    private final String text;

    public NewsGetter() throws IOException {
        try {
            document = Jsoup.connect(url).get();
            Elements elements = document.select("a.mg-card__link");
            if (elements.isEmpty())
                throw new NoSuchElementException("Не найден контент с новостюшками на сайте " + url);

            Element randomElement = elements.get(r(elements.size()));

            String text = randomElement
                    .select("h2")
                    .text();
            String link = ShortLinkGetter.getShortLink(randomElement.attr("href"));

            this.text = link + "\n" + text;

//            text = elements.stream()
//                    .map(element -> element.text() + "\n")
//                    .reduce((e1, e2) -> e1 + e2)
//                    .get();
        } catch (IOException e) {
            throw new IOException("Не могу подключиться к сайту " + url);
        } catch (Selector.SelectorParseException e) {
            throw new IOException("Невалидный CSS запрос в методе парсинга сайта " + url);
        }
    }

    public String getNews() {
        return text;
    }
}
