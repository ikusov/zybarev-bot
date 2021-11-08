package ru.ikusov.training.telegrambot.services;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.jsoup.select.Selector;

import java.io.IOException;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

import static ru.ikusov.training.telegrambot.utils.MyMath.r;

public class QuoteGetter {
    private final String URL0 = "https://www.livelib.ru/author/102587/quotes-arkadij-i-boris-strugatskie/~";
    private final int COUNT = 150;
    private final Document document;
    private final String text;

    public QuoteGetter() throws IOException {
        String url = URL0 + r(COUNT);
        try {
            document = Jsoup.connect(url).get();
            Elements elements = document.select("div.lenta-card");
            if (elements.isEmpty())
                throw new NoSuchElementException("Не найден контент с цитатками на сайте цитаток " + url);

//            //for testing purposes only
//            text = url + "\n" +
//                    elements.stream().map(element -> {
//                        String book = element.select("a.lenta-card__book-title").text();
//                        String quote = element.select("div#lenta-card__text-quote-escaped p").text();
//                        return "\n" + book + ":\n" + quote; })
//                    .filter(s -> s.length()>50
//                            && s.length()<300
//                            && !s.toLowerCase().contains("сборник")
//                            && !s.toLowerCase().contains("собрание"))
//                    .reduce((s1, s2) -> s1 + "\n" + s2)
//                    .get();

                    List<String> quotes =
                    elements.stream().map(element -> {
                        String book = element.select("a.lenta-card__book-title").text();
                        String quote = element.select("div#lenta-card__text-quote-escaped p").text();
                        return
//                                book + ":\n" +
                                quote; })
                    .filter(s -> s.length()>50
                            && s.length()<300
                            && !s.toLowerCase().contains("сборник")
                            && !s.toLowerCase().contains("собрание"))
                    .collect(Collectors.toList());
                    text = quotes.get(r(quotes.size()));

//            text = elements.get(r(elements.size()-1)).text();
        } catch (IOException e) {
            throw new IOException("Не могу подключиться к сайту цитаток " + url);
        } catch (Selector.SelectorParseException e) {
            throw new IOException("Невалидный CSS запрос в методе парсинга сайта цитаток " + url);
        }
    }

    public String getQuote() {
        return text;
    }
}
