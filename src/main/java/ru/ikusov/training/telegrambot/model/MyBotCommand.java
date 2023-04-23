package ru.ikusov.training.telegrambot.model;

import org.telegram.telegrambots.meta.api.objects.Chat;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.User;

public class MyBotCommand {
    private final String command;
    private final String params;
    private final String chatId;
    private final Integer topicId;
    private final User user;
    private final Chat chat;

    public MyBotCommand(String command, String params, Integer topicId, Chat chat, User user) {
        this.command = command;
        this.params = params;
        this.chat = chat;
        this.topicId = topicId;
        this.chatId = this.chat.getId().toString();
        this.user = user;
    }

    public static MyBotCommand buildCommand(Message message, String botUserName) {
        String msgText = message.getText();

        //if message is not command
        if (msgText.charAt(0) != '/') return null;

        //tokens of message: /command token1 token2 ... token2_000_047 ...
        String[] tokens = msgText.split(" ", 2);

        //parts of command: /command@bot@dunnowhat@itsridiculous
        String[] commandParts = tokens[0].split("@");

        //if for all that there is some ridiculous in the command
        if (commandParts.length > 1 && !commandParts[1].equals(botUserName)) {
            return null;
        }

        //use the same variable for MyBotCommand instance construction
        tokens[0] = commandParts[0].toLowerCase();
        return new MyBotCommand(
                tokens[0],
                tokens.length>1 ? tokens[1] : "",
                message.getMessageThreadId(),
                message.getChat(),
                message.getFrom()
        );
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

    public Chat getChat() {
        return chat;
    }

    public String getChatId() {
        return chatId;
    }

    public Integer getTopicId() {
        return topicId;
    }
}
