package ru.ikusov.training.telegrambot.botservices;

import org.telegram.telegrambots.meta.api.objects.Update;

public interface BotUpdateHandler {
    void handleUpdate(Update update);
}
