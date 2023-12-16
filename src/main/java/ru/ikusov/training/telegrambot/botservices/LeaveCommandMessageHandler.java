package ru.ikusov.training.telegrambot.botservices;

import org.springframework.stereotype.Component;
import ru.ikusov.training.telegrambot.model.CommandType;
import ru.ikusov.training.telegrambot.model.MyBotCommand;

@Component
public class LeaveCommandMessageHandler extends CommandMessageHandler {

    @Override
    protected CommandType getSupportedCommandType() {
        return CommandType.LEAVE;
    }

    @Override
    public BotReaction handleCommand(MyBotCommand command) {
        return new BotChatLeaver(command.chatId().toString());
    }
}
