package ru.ikusov.training.telegrambot.model.wordle;

import jakarta.persistence.*;
import ru.ikusov.training.telegrambot.model.CommonEntity;

@Entity
@Table(name = "wordle_word")
public class WordEntity implements CommonEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "word_id")
    private Long id;

    @Column(name = "word")
    private String word;

    public WordEntity(String word) {
        this.word = word;
    }

    public WordEntity() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getWord() {
        return word;
    }

    public void setWord(String word) {
        this.word = word;
    }
}
