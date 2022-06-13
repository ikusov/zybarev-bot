package ru.ikusov.training.telegrambot.services.wordle.dto;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.Serializable;

public class WordAttemptKey implements Serializable {
    private static final ObjectMapper objectMapper = new ObjectMapper();
    private final Long chatId;
    private final Long userId;
    private final String wordId;

    public static WordAttemptKey generateFromJSON(String JSONString) throws JsonProcessingException {
        return objectMapper.readValue(JSONString, WordAttemptKey.class);
    }

    public static String getJSONFrom(WordAttemptKey wak) throws JsonProcessingException {
        return objectMapper.writeValueAsString(wak);
    }

    public WordAttemptKey(Long chatId, Long userId, String wordId) {
        this.chatId = chatId;
        this.userId = userId;
        this.wordId = wordId;
    }

    public Long getChatId() {
        return chatId;
    }

    public Long getUserId() {
        return userId;
    }

    public String getWordId() {
        return wordId;
    }

    public String getJSON() throws JsonProcessingException {
        return objectMapper.writeValueAsString(this);
    }
}
