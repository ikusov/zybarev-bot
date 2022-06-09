package ru.ikusov.training.telegrambot.services.wordle;

import org.telegram.telegrambots.meta.api.objects.User;

public class WordleRepository2 {
    public String getCurrentWordForChat(Long chatId) {
        return null;
    }

    public String getLastTriedWordForChat(Long chatId) {
        return null;
    }

    public String getRandomWord() {
        return null;
    }

    public String getWordByText(String word) {
        return null;
    }

    public boolean isAnyWordAttempts(String currentWordEntity, Long chatId) {
        return false;
    }

    public int getOrCreateWordAttempt(User chatUser, Long chatId, int currentAllowedAttempts) {
        return 0;
    }

    public void saveWordAttempt(User chatUser, Long chatId, int wordAttempt) {

    }

    public void setLastTriedWord(String triedWord) {

    }

    public void setLastGuessedWord(String triedWord, Long chatId) {

    }
}
