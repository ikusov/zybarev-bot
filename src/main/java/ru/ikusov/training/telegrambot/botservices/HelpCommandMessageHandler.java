package ru.ikusov.training.telegrambot.botservices;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import ru.ikusov.training.telegrambot.model.MyBotCommand;

import java.util.Set;

@Component
@Order(100)
public class HelpCommandMessageHandler extends CommandMessageHandler {
    private final Set<String> commandVariants = Set.of("/help", "/h", "/?", "/помощь");

    @Override
    protected Set<String> getCommandVariants() {
        return commandVariants;
    }

    @Override
    public BotReaction handleCommand(MyBotCommand command) {
//        if (!commandVariants.contains(command.getCommand().toLowerCase())) return null;
//
        String textAnswer = registeredCommands.stream().reduce((s, s2) -> s + " " + s2).get();

        return new BotMessageSender(command.getChatId(), textAnswer);
    }
}
