package ru.ikusov.training.telegrambot.botservices;

import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import ru.ikusov.training.telegrambot.botservices.annotation.ExcludeFromHelp;
import ru.ikusov.training.telegrambot.model.MyBotCommand;

import java.util.Set;

@Component
@Order(500)
public class HelpCommandMessageHandler extends CommandMessageHandler {

    @Override
    protected Set<String> getCommandVariants() {
        return Set.of("/help", "/h", "/?", "/помощь");
    }

    @Override
    @ExcludeFromHelp
    protected String getHelpString() {
return "Список команд";
}

    @Override
    public BotReaction handleCommand(MyBotCommand command) {
        String textAnswer = helpString;

        return new BotMessageSender(command.getChatId(), command.getTopicId(), textAnswer);
    }
}
