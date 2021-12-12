package ru.ikusov.training.telegrambot.botservices;

import org.telegram.telegrambots.meta.api.objects.Update;

/**
 * interface for handle bot updates (messages, changed status, user entering/leaving etc
 */
public interface BotUpdateHandler {
    void handleUpdate(Update update);
}
