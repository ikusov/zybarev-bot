package ru.ikusov.training.telegrambot.model;

import org.telegram.telegrambots.meta.api.objects.Chat;
import org.telegram.telegrambots.meta.api.objects.User;

public class MyBotCommand {
    private final String command, params, chatId;
    private final User user;
    private final Chat chat;

    public MyBotCommand(String command, String params, Chat chat, User user) {
        this.command = command;
        this.params = params;
        this.chat = chat;
        this.user = user;
        this.chatId = chat.getId().toString();
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

    public Chat getChat() { return chat; }

    public String getChatId() {
        return chatId;
    }
}
