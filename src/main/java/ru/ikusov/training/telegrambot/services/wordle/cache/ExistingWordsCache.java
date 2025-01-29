package ru.ikusov.training.telegrambot.services.wordle.cache;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.ikusov.training.telegrambot.dao.DatabaseConnector;
import ru.ikusov.training.telegrambot.model.wordle.WordEntity;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@Component
public class ExistingWordsCache {
    private final Set<String> existingWords;

    public ExistingWordsCache(DatabaseConnector databaseConnector) {
        Set<String> words = Set.of();
        try {
            log.debug("Наполнение кэша проверочных слов из БД...");
            words = databaseConnector.getByQuery(WordEntity.class, "from WordEntity")
                    .stream()
                    .map(WordEntity::getWord)
                    .collect(Collectors.toCollection(HashSet::new));
            log.debug("Наполнение кэша проверочных слов окончено! В кэше {} слов!", words.size());
        } catch (Exception ignored) {
            log.debug("Наполнение кэша проверочных слов пало! В кэше {} слов!", words.size());
        }
        existingWords = words;
    }

    public boolean contains(String word) {
        boolean contains = existingWords.contains(word);
        log.debug("Слово '{}' в кэше проверочных слов: {}сутствует!", word, contains ? "при" : "ОТ");
        return contains;
    }

    public void add(String word) {
        existingWords.add(word);
        log.debug("Слово '{}' добавлено в кэш проверочных слов! В кэше теперь {} слов!", word, existingWords.size());
    }
}
