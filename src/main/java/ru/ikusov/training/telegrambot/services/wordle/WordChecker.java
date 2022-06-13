package ru.ikusov.training.telegrambot.services.wordle;

import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public interface WordChecker {
    boolean check(String word) throws IOException;
}
