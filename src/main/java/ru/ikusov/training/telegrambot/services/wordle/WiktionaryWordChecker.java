package ru.ikusov.training.telegrambot.services.wordle;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.jsoup.select.Selector;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

@Component
public class WiktionaryWordChecker implements WordChecker {
    private final Logger log = LoggerFactory.getLogger(this.getClass());

    private final String URL = "https://ru.wiktionary.org/wiki/";
    @Override
    public boolean check(String word) throws IOException {
        final String NOUN_TEXT = "Существительное";
        final String url = URL + URLEncoder.encode(word, StandardCharsets.UTF_8);
        Document document;
        try {
            log.debug("Подключаемся к ресурсу '{}' для проверки существования существительного '{}'", URL, word);
            document = Jsoup.connect(url).get();
            Elements elements = document.select("div.mw-parser-output > p > a");
            for (Element element : elements) {
                if (element.text().equals(NOUN_TEXT)) {
                    log.debug("Проверка увенчалась успехом!");
                    return true;
                }
            }
        } catch (IOException e) {
            log.debug("Не найден URL '{}' (слово не существует либо ошибка сети)", url);
            throw e;
        } catch (Selector.SelectorParseException e) {
            log.debug("Невалидный CSS запрос в методе парсинга базы русских слов '{}'", url);
            throw new IOException(e);
        }

        log.debug("Существительное '{}' не найдено на ресурсе '{}'", word, URL);
        return false;
    }
}
