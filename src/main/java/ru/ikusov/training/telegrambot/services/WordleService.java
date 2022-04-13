package ru.ikusov.training.telegrambot.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class WordleService {
    private DatabaseConnector databaseConnector;

    @Autowired
    public WordleService(DatabaseConnector databaseConnector) {
        this.databaseConnector = databaseConnector;
    }

    public String checkWord(String word) {
        if (!wordIsInDB(word)) {
            throw new RuntimeException("No word in DB!");
        }

        return "";
    }

    private boolean wordIsInDB(String word) {
        return false;
    }
}
