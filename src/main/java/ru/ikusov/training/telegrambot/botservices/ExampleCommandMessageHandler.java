package ru.ikusov.training.telegrambot.botservices;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import ru.ikusov.training.telegrambot.model.MyBotCommand;
import ru.ikusov.training.telegrambot.services.ExampleProvider;

import java.util.Set;

@Component
@Order(20)
public class ExampleCommandMessageHandler extends CommandMessageHandler {
    private final Set<String> commandVariants = Set.of("/example", "/e", "/primer", "/пример");

    @Autowired
    private ExampleProvider exampleProvider;

    @Override
    protected void addHelp() {
        String help = commandVariants.stream().reduce((s1, s2) -> s1 + ", " + s2).orElse("");
        help += " - арифметический пример. Разомни мозги!\n";
        helpString = help + helpString;
    }

    @Override
    protected Set<String> getCommandVariants() {
        return commandVariants;
    }

    @Override
    public BotReaction handleCommand(MyBotCommand command) {
        long userId = command.getUser().getId();

        ExampleProvider.Complexity complexity = userId == 1834473953 ? ExampleProvider.Complexity.EASY :
                ExampleProvider.Complexity.getRandomComplexity();

        String textAnswer;
        textAnswer = "Сколько будет " +
                    (exampleProvider.isAnswered() ? exampleProvider.generateExampleNew(complexity) : exampleProvider.getExample()) +
                    "=?";

        return new BotMessageSender(command.getChatId(), command.getTopicId(), textAnswer);
    }
}
