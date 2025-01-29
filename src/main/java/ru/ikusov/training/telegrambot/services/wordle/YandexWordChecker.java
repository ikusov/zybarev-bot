package ru.ikusov.training.telegrambot.services.wordle;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import ru.ikusov.training.telegrambot.services.HttpConnector;
import ru.ikusov.training.telegrambot.services.wordle.cache.ExistingWordsCache;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

import static ru.ikusov.training.telegrambot.MainClass.DICTIONARY_API_KEY;

@Slf4j
@Order(30)
@Component
@RequiredArgsConstructor
public class YandexWordChecker implements WordChecker {

    private final ExistingWordsCache wordsCache;

    private final String URL = "https://dictionary.yandex.net/api/v1/dicservice.json/lookup?key=" + DICTIONARY_API_KEY + "&lang=ru-ru&text=";

    @Override
    public String getName() {
        return "dictionary.yandex.net";
    }

    @Override
    public boolean check(String word) throws IOException {
        final String NOUN_TEXT = "noun";
        final String url = URL + URLEncoder.encode(word, StandardCharsets.UTF_8);
        try {
            log.debug("Подключаемся к ресурсу '{}' для проверки существования существительного '{}'", URL, word);
            String jsonString = new HttpConnector(url).getJsonString();
            log.debug("Получена строка: {}", jsonString);
            ArrayNode wordDefList = (ArrayNode) parseJson(jsonString).get("def");
            log.debug("Получен список определений: {}", wordDefList);
            for (JsonNode wordDef : wordDefList) {
                JsonNode pos = wordDef.get("pos");
                if (pos != null && NOUN_TEXT.equals(pos.asText())) {
                    log.debug("Существительное '{}' надено на ресурсе '{}'", word, URL);
                    wordsCache.add(word);
                    return true;
                }
            }
        } catch (JsonProcessingException e) {
            log.debug("Получен невалидный JSON от сервиса Yandex.Словарь");
            throw e;
        } catch (IOException e) {
            log.debug("Ошибка при отправке запроса на URL '{}'", url);
            throw e;
        }
        log.debug("Существительное '{}' не найдено на ресурсе '{}'", word, URL);
        return false;
    }

    private JsonNode parseJson(String jsonString) throws JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();
        return mapper.readTree(jsonString);
    }
}
