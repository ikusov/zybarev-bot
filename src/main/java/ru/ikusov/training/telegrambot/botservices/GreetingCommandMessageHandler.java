package ru.ikusov.training.telegrambot.botservices;

import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import ru.ikusov.training.telegrambot.model.MyBotCommand;
import ru.ikusov.training.telegrambot.utils.MessageType;

import java.util.Set;

@Component
@Order(30)
public class GreetingCommandMessageHandler extends CommandMessageHandler {
    private final Set<String> commandVariants = Set.of("/bot", "/hello", "/пт", "/бот", "/привет");

    @Override
    protected Set<String> getCommandVariants() {
        return commandVariants;
    }

    @Override
    protected void addHelp() {
        String help = commandVariants.stream().reduce((s1, s2) -> s1 + ", " + s2).orElse("");
        help += " - приветствие от бота\n";
        helpString = help + helpString;
    }

    @Override
    public BotReaction handleCommand(MyBotCommand command) {
        String userName = command.getUser().getFirstName();

        String textAnswer =
//                RandomMessageGenerator.generate(RandomMessageGenerator.MessageType.GREETING_MESSAGE, userName);
                String.format(
                    MessageType.GREETING_MESSAGE.getRandomMessage(),
                    userName);

        return new BotMessageSender(command.getChatId(), textAnswer);
    }
}
