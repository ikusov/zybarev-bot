package ru.ikusov.training.telegrambot.botservices;

import org.springframework.stereotype.Component;
import ru.ikusov.training.telegrambot.model.CommandType;
import ru.ikusov.training.telegrambot.model.MyBotCommand;
import ru.ikusov.training.telegrambot.services.RhymeGetter;

@Component
public class RhymeCommandMessageHandler extends CommandMessageHandler {
    @Override
    protected CommandType getSupportedCommandType() {
        return CommandType.RHYME;
    }

    @Override
    public BotReaction handleCommand(MyBotCommand command) {
        RhymeGetter rhymeGetter;
        String textAnswer;

        try {
            rhymeGetter = new RhymeGetter();
            textAnswer = rhymeGetter.getRhyme();
        } catch (Exception e) {
            textAnswer = e.getMessage();
        }

        return new BotMessageSender(command.chatId(), command.topicId(), textAnswer);
    }
}
