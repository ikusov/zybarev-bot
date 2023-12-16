package ru.ikusov.training.telegrambot.botservices;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import ru.ikusov.training.telegrambot.model.CommandType;
import ru.ikusov.training.telegrambot.model.MyBotCommand;
import ru.ikusov.training.telegrambot.services.CaseQuestionGenerator;

@Component
public class CaseCommandMessageHandler extends CommandMessageHandler {
    @Autowired
    private CaseQuestionGenerator caseQuestionGenerator;

    @Override
    protected CommandType getSupportedCommandType() {
        return CommandType.CASE;
    }

    @Override
    public BotReaction handleCommand(MyBotCommand command) {
        String textAnswer;

        try {
            textAnswer = caseQuestionGenerator.getQuestion();
        } catch (Exception e) {
            textAnswer = e.getMessage();
        }

        return new BotMessageSender(command.chatId(), command.topicId(), textAnswer);
    }
}
