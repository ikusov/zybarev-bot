package ru.ikusov.training.telegrambot.model.wordle;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import ru.ikusov.training.telegrambot.model.CommonEntity;

@Entity
@Table(name = "wordle_chat_current_word")
public class WordleChatCurrentWordEntity implements CommonEntity {
    @Id
    @Column(name = "chat_id")
    private Long chatId;
    @Column(name = "current_word")
    private String currentWord;

    public Long getChatId() {
        return chatId;
    }

    public void setChatId(Long chatId) {
        this.chatId = chatId;
    }

    public String getCurrentWord() {
        return currentWord;
    }

    public void setCurrentWord(String currentWord) {
        this.currentWord = currentWord;
    }

    public WordleChatCurrentWordEntity(Long chatId, String currentWord) {
        this.chatId = chatId;
        this.currentWord = currentWord;
    }

    public WordleChatCurrentWordEntity() {
    }
}
