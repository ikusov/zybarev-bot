package ru.ikusov.training.telegrambot.botservices;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import ru.ikusov.training.telegrambot.Bot;
import ru.ikusov.training.telegrambot.services.UserNameGetter;

import javax.annotation.PostConstruct;
import java.time.LocalDateTime;
import java.util.List;

@Component
public class MessageTypeUpdatesHandler implements BotUpdateHandler {

    @Autowired
    List<MessageHandler> messageHandlers;

    @Override
    public void handleUpdate(Update update) {

        if(!update.hasMessage()) return;

        Message message = update.getMessage();
        if (!message.hasText()) return;

        messageHandlers.forEach(messageHandler -> messageHandler.handleMessage(message));
    }
}
