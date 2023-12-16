package ru.ikusov.training.telegrambot.botservices;

import org.springframework.stereotype.Component;
import ru.ikusov.training.telegrambot.model.CommandType;
import ru.ikusov.training.telegrambot.model.MyBotCommand;
import ru.ikusov.training.telegrambot.services.DonateMessageHardcodedProvider;
import ru.ikusov.training.telegrambot.services.DonateMessageProvider;

@Component
public class DonateCommandMessageHandler extends CommandMessageHandler {
    private final DonateMessageProvider donateMessageProvider = new DonateMessageHardcodedProvider();

    @Override
    protected CommandType getSupportedCommandType() {
        return CommandType.DONATE;
    }

    @Override
    public BotReaction handleCommand(MyBotCommand command) {
        String msgText = donateMessageProvider.fetchMessage();
        return new BotMessageSender(command.chatId(), command.topicId(), msgText);
    }
}
