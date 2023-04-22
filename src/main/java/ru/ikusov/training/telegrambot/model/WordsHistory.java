package ru.ikusov.training.telegrambot.model;

import jakarta.persistence.*;

@Entity
@Table(name = "words_history")
public class WordsHistory implements CommonEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "word_id")
    private Long wordId;

    @Column(name = "chat_id")
    private Long chatId;

    @Column(name = "is_guessed")
    private boolean isGuessed;

    @Column(name = "guesser_id")
    private Long guesserId;

    public WordsHistory() {
    }

    public WordsHistory(Long wordId, Long chatId, boolean isGuessed, Long guesserId) {
        this.wordId = wordId;
        this.chatId = chatId;
        this.isGuessed = isGuessed;
        this.guesserId = guesserId;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getWordId() {
        return wordId;
    }

    public void setWordId(Long wordId) {
        this.wordId = wordId;
    }

    public Long getChatId() {
        return chatId;
    }

    public void setChatId(Long chatId) {
        this.chatId = chatId;
    }

    public boolean isGuessed() {
        return isGuessed;
    }

    public void setGuessed(boolean guessed) {
        isGuessed = guessed;
    }

    public Long getGuesserId() {
        return guesserId;
    }

    public void setGuesserId(Long guesserId) {
        this.guesserId = guesserId;
    }
}
