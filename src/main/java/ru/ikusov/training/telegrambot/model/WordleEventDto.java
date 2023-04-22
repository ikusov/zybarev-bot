package ru.ikusov.training.telegrambot.model;

public class WordleEventDto {
    private final Long chatId;
    private final Long userId;
    private final String currentWord;
    private final String attemptWord;
    private final boolean isRight;

    public WordleEventDto(Long chatId, Long userId, String currentWord, String attemptWord, boolean isRight) {
        this.chatId = chatId;
        this.userId = userId;
        this.currentWord = currentWord;
        this.attemptWord = attemptWord;
        this.isRight = isRight;
    }

    public Long getChatId() {
        return chatId;
    }

    public Long getUserId() {
        return userId;
    }

    public String getCurrentWord() {
        return currentWord;
    }

    public String getAttemptWord() {
        return attemptWord;
    }

    public boolean isRight() {
        return isRight;
    }
}
