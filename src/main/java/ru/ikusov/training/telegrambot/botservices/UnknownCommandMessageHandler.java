package ru.ikusov.training.telegrambot.botservices;

import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import ru.ikusov.training.telegrambot.model.CommandType;
import ru.ikusov.training.telegrambot.model.MyBotCommand;
import ru.ikusov.training.telegrambot.utils.MessageType;

@Component
@Order
public class UnknownCommandMessageHandler extends CommandMessageHandler {
    @Override
    protected CommandType getSupportedCommandType() {
        return CommandType.UNKNOWN;
    }

    @Override
    public BotReaction handleCommand(MyBotCommand command) {
        String textAnswer =
                String.format(
                        MessageType.UNKNOWN_COMMAND_MESSAGE.getRandomMessage(),
                        command.commandText());
        return new BotMessageSender(command.chatId(), command.topicId(), textAnswer);
    }
}
