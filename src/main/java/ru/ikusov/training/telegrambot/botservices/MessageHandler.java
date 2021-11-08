package ru.ikusov.training.telegrambot.botservices;

import org.telegram.telegrambots.meta.api.objects.Message;
import ru.ikusov.training.telegrambot.Bot;

public interface MessageHandler {
    void handleMessage(Message message);
}
