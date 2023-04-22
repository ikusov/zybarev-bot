package ru.ikusov.training.telegrambot.botservices;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Chat;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.User;
import ru.ikusov.training.telegrambot.dao.DatabaseConnector;
import ru.ikusov.training.telegrambot.model.ChatEntity;
import ru.ikusov.training.telegrambot.model.UserEntity;

import java.util.List;

@Component
public class MessageTypeUpdatesHandler implements BotUpdateHandler {

    @Autowired
    List<MessageHandler> messageHandlers;
    @Autowired
    DatabaseConnector databaseConnector;

    @Override
    public void handleUpdate(Update update) {
        if(!update.hasMessage()) return;

        Message message = update.getMessage();
        try {
            User user = message.getFrom();
            databaseConnector.saveOrUpdate(new UserEntity(user));
            Chat chat = message.getChat();
            databaseConnector.saveOrUpdate(new ChatEntity(chat));
        } catch (Exception ignore) {}

        if (!message.hasText()) return;

        messageHandlers.forEach(messageHandler -> messageHandler.handleMessage(message));
    }
}
