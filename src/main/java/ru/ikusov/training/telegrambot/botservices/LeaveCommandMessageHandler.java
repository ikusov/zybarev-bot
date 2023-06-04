package ru.ikusov.training.telegrambot.botservices;

import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import ru.ikusov.training.telegrambot.model.MyBotCommand;

import java.util.Set;

@Component
@Order(40)
public class LeaveCommandMessageHandler extends CommandMessageHandler {
    private final Set<String> commandVariants = Set.of("/leave", "/goout", "/изыди");

    @Override
    protected Set<String> getCommandVariants() {
        return commandVariants;
    }

    @Override
    protected void addHelp() {
        String help = commandVariants.stream().reduce((s1, s2) -> s1 + ", " + s2).orElse("");
        help += " - сделать бота покинуть чат.\n";
        helpString = help + helpString;
    }

    @Override
    public BotReaction handleCommand(MyBotCommand command) {
//        if (!commandVariants.contains(command.getCommand().toLowerCase())) return null;
//
        return new BotChatLeaver(command.getChatId().toString());
    }
}
