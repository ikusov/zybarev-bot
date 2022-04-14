package ru.ikusov.training.telegrambot.model;

import javax.persistence.*;

@Entity
@Table(name = "words_in_game")
public class WordInGameEntity implements CommonEntity {
    @Id
    @OneToOne
    @JoinColumn(name = "word_id")
    private WordEntity word;
}
