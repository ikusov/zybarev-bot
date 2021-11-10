package ru.ikusov.training.telegrambot;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.objects.Update;
import ru.ikusov.training.telegrambot.botservices.BotUpdateHandler;

import java.util.List;

@Component("bot")
public class Bot extends TelegramLongPollingBot {
    //используем здесь аннотацию вялую
    @Value("${bot.name}")
    private String botUserName;

    @Value("${bot.token}")
    private String botToken;

    @Autowired
    private List<BotUpdateHandler> botUpdateHandlers;

    public Bot() {
    }

    @Override
    public String getBotUsername() {
        return botUserName;
    }

    @Override
    public String getBotToken() {
        return botToken;
    }

    @Override
    public void onUpdateReceived(Update update) {
        botUpdateHandlers.forEach(botUpdateHandler -> botUpdateHandler.handleUpdate(update));
    }
}