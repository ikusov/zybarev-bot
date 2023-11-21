package ru.ikusov.training.telegrambot.botservices;

import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import ru.ikusov.training.telegrambot.botservices.annotation.ExcludeFromHelp;
import ru.ikusov.training.telegrambot.model.MyBotCommand;

import java.util.Set;

@Component
@Order(40)
public class LeaveCommandMessageHandler extends CommandMessageHandler {

    @Override
    protected Set<String> getCommandVariants() {
        return Set.of("/leave", "/goout", "/изыди");
    }

    @Override
    @ExcludeFromHelp
    protected String getHelpString() {
return "сделать бота покинуть чат";
}

    @Override
    public BotReaction handleCommand(MyBotCommand command) {
//        if (!commandVariants.contains(command.getCommand().toLowerCase())) return null;
//
        return new BotChatLeaver(command.getChatId().toString());
    }
}
