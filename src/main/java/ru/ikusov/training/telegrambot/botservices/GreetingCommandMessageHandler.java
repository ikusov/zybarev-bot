package ru.ikusov.training.telegrambot.botservices;

import org.springframework.stereotype.Component;
import ru.ikusov.training.telegrambot.model.CommandType;
import ru.ikusov.training.telegrambot.model.MyBotCommand;
import ru.ikusov.training.telegrambot.utils.MessageType;

@Component
public class GreetingCommandMessageHandler extends CommandMessageHandler {

    @Override
    protected CommandType getSupportedCommandType() {
        return CommandType.GREETING;
    }

    @Override
    public BotReaction handleCommand(MyBotCommand command) {
        String userName = command.user().getFirstName();

        String textAnswer =
                String.format(
                    MessageType.HNY_GREETING_MESSAGE.getRandomMessage(),
                    userName);

        return new BotMessageSender(command.chatId(), command.topicId(), textAnswer);
    }
}
