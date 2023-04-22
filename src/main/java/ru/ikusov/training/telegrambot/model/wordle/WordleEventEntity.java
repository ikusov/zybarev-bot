package ru.ikusov.training.telegrambot.model.wordle;

import jakarta.persistence.*;
import ru.ikusov.training.telegrambot.model.CommonEntity;

@Entity
@Table(name = "wordle_event")
public class WordleEventEntity implements CommonEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;
    @Column(name = "timestamp")
    private Long timestamp;
    @Column(name = "chat_id")
    private Long chatId;
    @Column(name = "user_id")
    private Long userId;
    @Column(name = "current_word")
    private String currentWord;
    @Column(name = "attempt_word")
    private String attemptWord;
    @Column(name = "is_right")
    private boolean right;

    public WordleEventEntity(Long timestamp, Long chatId, Long userId, String currentWord, String attemptWord, boolean right) {
        this.timestamp = timestamp;
        this.chatId = chatId;
        this.userId = userId;
        this.currentWord = currentWord;
        this.attemptWord = attemptWord;
        this.right = right;
    }

    public WordleEventEntity() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Long timestamp) {
        this.timestamp = timestamp;
    }

    public Long getChatId() {
        return chatId;
    }

    public void setChatId(Long chatId) {
        this.chatId = chatId;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getCurrentWord() {
        return currentWord;
    }

    public void setCurrentWord(String currentWord) {
        this.currentWord = currentWord;
    }

    public String getAttemptWord() {
        return attemptWord;
    }

    public void setAttemptWord(String attemptWord) {
        this.attemptWord = attemptWord;
    }

    public boolean isRight() {
        return right;
    }

    public void setRight(boolean right) {
        this.right = right;
    }
}
