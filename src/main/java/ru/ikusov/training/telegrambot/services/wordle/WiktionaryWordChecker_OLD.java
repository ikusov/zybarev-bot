package ru.ikusov.training.telegrambot.services.wordle;

import lombok.extern.slf4j.Slf4j;
import org.jsoup.HttpStatusException;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.jsoup.select.Selector;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

@Slf4j
@Component
@Deprecated
// todo: заиспользовать новый класс после прочёсывания БД с выпилом слов, не проходящих чекер
public class WiktionaryWordChecker_OLD implements WordChecker {
    private final String URL = "https://ru.wiktionary.org/wiki/";

    @Override
    public String getName() {
        return "ru.wiktionary.org";
    }

    @Override
    public boolean check(String word) throws IOException {
        final String url = URL + URLEncoder.encode(word, StandardCharsets.UTF_8);
        Document document;
        try {
            log.debug("Подключаемся к ресурсу '{}' для проверки существования существительного '{}'", URL, word);
            document = Jsoup.connect(url).get();
            if (isDocumentForNoun(document, word)) {
                return true;
            }
        } catch (HttpStatusException e) {
            log.debug("Существительное '{}' не найдено на ресурсе '{}'", word, url);
            return false;
        } catch (IOException e) {
            log.error("Ошибка при отправке запроса на URL '{}'", url);
            throw e;
        } catch (Selector.SelectorParseException e) {
            log.error("Невалидный CSS запрос в методе парсинга базы русских слов '{}'", url);
            throw new IOException(e);
        }

        log.debug("Существительное '{}' не найдено на ресурсе '{}'", word, URL);
        return false;
    }

    private boolean isDocumentForNoun(Document document, String word) {
        final String NOUN_TEXT = "Существительное";

        Elements elements = document.select("div.mw-parser-output > p > a");
        for (Element element : elements) {
            if (element.text().equals(NOUN_TEXT)) {
                log.debug("Слово {} является существительным!", word);
                return true;
            }
        }
        return false;
    }

}
