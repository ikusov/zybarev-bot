package ru.ikusov.training.telegrambot.services.wordle;

import org.springframework.stereotype.Component;

import java.io.IOException;

//TODO: добавить вордчекер из БД, чтобы обезопаситься от проблем с внешними сервисами
@Component
public interface WordChecker {
    boolean check(String word) throws IOException;
}
