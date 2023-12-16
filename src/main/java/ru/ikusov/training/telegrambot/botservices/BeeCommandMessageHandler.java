package ru.ikusov.training.telegrambot.botservices;

import org.springframework.stereotype.Component;
import ru.ikusov.training.telegrambot.model.CommandType;
import ru.ikusov.training.telegrambot.model.MyBotCommand;

@Component
public class BeeCommandMessageHandler extends CommandMessageHandler {
    @Override
    protected CommandType getSupportedCommandType() {
        return CommandType.BEE;
    }

    @Override
    public BotReaction handleCommand(MyBotCommand command) {
        return new BotFormattedMessageSender(
                command.chatId(),
                command.topicId(),
                """
                        \uD83D\uDC1D
                        Ты пчела я пчеловод
                        А мы любим мёд
                        А мне повезёт
                        С тобой мне повезёт"""
        );
    }
}
