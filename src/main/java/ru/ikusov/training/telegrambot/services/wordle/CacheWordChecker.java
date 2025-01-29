package ru.ikusov.training.telegrambot.services.wordle;

import lombok.RequiredArgsConstructor;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import ru.ikusov.training.telegrambot.services.wordle.cache.ExistingWordsCache;

import java.io.IOException;

@Order(10)
@Component
@RequiredArgsConstructor
public class CacheWordChecker implements WordChecker {
    private final ExistingWordsCache wordsCache;

    @Override
    public String getName() {
        return "zybarev.inmemory.cache";
    }

    @Override
    public boolean check(String word) throws IOException {
        return wordsCache.contains(word);
    }
}
