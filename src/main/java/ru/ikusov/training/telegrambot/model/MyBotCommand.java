package ru.ikusov.training.telegrambot.model;

import org.telegram.telegrambots.meta.api.objects.Chat;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.User;

import java.util.Arrays;

public record MyBotCommand(
        CommandType commandType,
        String commandText,
        String params,
        Long chatId,
        Integer topicId,
        User user,
        Chat chat
) {
    public static MyBotCommand build(Message message, String botUserName) {
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

        String commandString = commandParts[0].substring(1);
        CommandType commandType = Arrays.stream(CommandType.values())
                .filter(c -> c.getAliases().contains(commandString))
                .findAny()
                .orElse(CommandType.UNKNOWN);

        //use the same variable for MyBotCommand instance construction
        tokens[0] = commandParts[0].toLowerCase();
        return new MyBotCommand(
                commandType,
                commandParts[0],
                tokens.length > 1 ? tokens[1] : "",
                message.getChat().getId(),
                message.getMessageThreadId(),
                message.getFrom(),
                message.getChat()
        );
    }
}
