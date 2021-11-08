package ru.ikusov.training.telegrambot.botservices;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import ru.ikusov.training.telegrambot.Bot;
import ru.ikusov.training.telegrambot.model.MyBotCommand;
import ru.ikusov.training.telegrambot.utils.RandomMessageGenerator;

import java.util.Set;

@Component
@Order(30)
public class GreetingCommandMessageHandler extends CommandMessageHandler {
    private final Set<String> commandVariants = Set.of("/bot", "/hello", "/пт", "/бот");

    @Override
    protected Set<String> getCommandVariants() {
        return commandVariants;
    }

    @Override
    public BotReaction handleCommand(MyBotCommand command) {
        String userName = command.getUser().getFirstName();

        String textAnswer = RandomMessageGenerator.generate(RandomMessageGenerator.MessageType.GREETING_MESSAGE, userName);

        return new BotMessageSender(command.getChatId(), textAnswer);
    }
}
