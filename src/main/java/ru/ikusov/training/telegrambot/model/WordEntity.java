package ru.ikusov.training.telegrambot.model;

import javax.persistence.*;

@Entity
@Table(name = "words")
public class WordEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private int id;

    @Column(name = "text")
    private String text;

    public WordEntity() {
    }

    public WordEntity(int id, String text) {
        this.id = id;
        this.text = text;
    }

    public int getId() {
        return id;
    }

    public WordEntity setId(int id) {
        this.id = id;
        return this;
    }

    public String getText() {
        return text;
    }

    public WordEntity setText(String text) {
        this.text = text;
        return this;
    }
}
