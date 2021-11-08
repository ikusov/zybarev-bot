package ru.ikusov.training.telegrambot.model;

import org.telegram.telegrambots.meta.api.objects.User;

public class MyBotCommand {
    private final String command, params, chatId;
    private final User user;

    public MyBotCommand(String command, String params, String chatId, User user) {
        this.command = command;
        this.params = params;
        this.chatId = chatId;
        this.user = user;
    }

    public String getCommand() {
        return command;
    }

    public String getParams() {
        return params;
    }

    public User getUser() {
        return user;
    }

    public String getChatId() {
        return chatId;
    }
}
