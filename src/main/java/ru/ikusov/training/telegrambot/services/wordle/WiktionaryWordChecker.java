package ru.ikusov.training.telegrambot.services.wordle;

import lombok.extern.slf4j.Slf4j;
import org.jsoup.HttpStatusException;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.jsoup.select.Selector;
import ru.ikusov.training.telegrambot.utils.MyString;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

@Slf4j
public class WiktionaryWordChecker implements WordChecker {
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
            if (isDocumentForNoun(document, word) && isDocumentForSingularSubjective(document, word)) {
                return true;
            }
        } catch (HttpStatusException ignored) {
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

    private boolean isDocumentForSingularSubjective(Document document, String word) {
        try {
            Elements morphotableRows = document.select("table.morfotable > tbody > tr");

            Element headerFirstColumn = morphotableRows.get(0).select("td,th").get(0);
            boolean isHeaderContainsCase = headerFirstColumn.text().toLowerCase().contains("падеж");

            Elements secondRowColumns = morphotableRows.get(1).select("td");

            String secondRowFirstColumnText = secondRowColumns.get(0).text();
            boolean isSecondRowFirstColumnContainsSubjective = secondRowFirstColumnText.toLowerCase().contains("им");

            String secondRowSecondColumnText = secondRowColumns.get(1).text();
            boolean isSecondRowSecondColumnTextDiffersNoMoreThanOneSymbol =
                    MyString.areEqualRussianWords(word, secondRowSecondColumnText);

            if (isHeaderContainsCase && isSecondRowFirstColumnContainsSubjective) {
                log.debug("С ресурса wiktionary.org получены данные о том, что '{}' - сущ им. п. ед. ч.!", word);
                return isSecondRowSecondColumnTextDiffersNoMoreThanOneSymbol;
            }
        } catch (Exception exception) {
            log.error("Возникла ошибка при парсинге Wiktionary страницы для слова '{}'", word, exception);
            //ексепшен глотаем и возвращаем тру
        }

        return true;
    }
}
