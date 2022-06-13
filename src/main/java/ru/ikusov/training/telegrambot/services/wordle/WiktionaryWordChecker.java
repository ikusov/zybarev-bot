package ru.ikusov.training.telegrambot.services.wordle;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.jsoup.select.Selector;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

@Component
public class WiktionaryWordChecker implements WordChecker {
    private String URL = "https://ru.wiktionary.org/wiki/";
    @Override
    public boolean check(String word) throws IOException {
        final String NOUN_TEXT = "Существительное";
        final String url = URL + URLEncoder.encode(word, StandardCharsets.UTF_8);
        Document document;
        try {
            document = Jsoup.connect(url).get();
            Elements elements = document.select("div.mw-parser-output > p > a");
            for (Element element : elements) {
                if (element.text().equals(NOUN_TEXT)) {
                    return true;
                }
//                System.out.println(element.text());
            }
        } catch (IOException e) {
            throw new IOException("Не могу подключиться к базе русских слов " + url);
        } catch (Selector.SelectorParseException e) {
            throw new IOException("Невалидный CSS запрос в методе парсинга базы русских слов " + url);
        }

        return false;
    }
}
