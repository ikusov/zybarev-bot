package ru.ikusov.training.telegrambot.botservices;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import ru.ikusov.training.telegrambot.model.CommandType;
import ru.ikusov.training.telegrambot.model.MyBotCommand;
import ru.ikusov.training.telegrambot.services.ExampleProvider;

@Component
public class ExampleCommandMessageHandler extends CommandMessageHandler {

    @Autowired
    private ExampleProvider exampleProvider;

    @Override
    protected CommandType getSupportedCommandType() {
        return CommandType.EXAMPLE;
    }

    @Override
    public BotReaction handleCommand(MyBotCommand command) {
        long userId = command.user().getId();

        ExampleProvider.Complexity complexity = userId == 1834473953 ? ExampleProvider.Complexity.EASY :
                ExampleProvider.Complexity.getRandomComplexity();

        String textAnswer;
        textAnswer = "Сколько будет " +
                (exampleProvider.isAnswered() ? exampleProvider.generateExampleNew(complexity) : exampleProvider.getExample()) +
                "=?";

        return new BotMessageSender(command.chatId(), command.topicId(), textAnswer);
    }
}
