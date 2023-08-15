package ru.ikusov.training.telegrambot.services.wordle.dto;

import java.util.List;

public record WordleChatStatDto (
        boolean isGameStarted,
        List<String> attemptList
) {}
