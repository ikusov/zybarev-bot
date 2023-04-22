package ru.ikusov.training.telegrambot.botservices;

import org.telegram.telegrambots.meta.api.objects.Message;

public interface MessageHandler {
    void handleMessage(Message message);
}
