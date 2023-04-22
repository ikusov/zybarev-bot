package ru.ikusov.training.telegrambot.services.wordle;

import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.User;

@Component
public interface WordleService {
    String startGame(Long chatId, Long userId, int wordLen);

    String checkWord(String word, User chatUser, Long chatId);

    boolean isWordleAnswer(String text, Long chatId);
}
