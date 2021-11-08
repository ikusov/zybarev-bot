package ru.ikusov.training.telegrambot.botservices;

import org.telegram.telegrambots.meta.api.objects.Update;
import ru.ikusov.training.telegrambot.Bot;

public interface BotUpdateHandler {
    void handleUpdate(Update update);
}
