package ru.ikusov.training.telegrambot.model;

import javax.persistence.*;

@Entity
@Table(name = "word_attempt")
public class WordAttempt implements CommonEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "word_id")
    private Long wordId;

    @Column(name = "user_id")
    private Long userId;

    @Column(name = "attempts_count")
    private Integer attemptsCount;

    @Column(name = "is_guessed")
    private boolean isGuessed;

    public WordAttempt() {
    }

    public WordAttempt(Long wordId, Long userId) {
        this.wordId = wordId;
        this.userId = userId;
    }
}
