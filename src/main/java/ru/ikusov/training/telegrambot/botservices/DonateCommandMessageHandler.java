package ru.ikusov.training.telegrambot.botservices;

import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import ru.ikusov.training.telegrambot.model.MyBotCommand;
import ru.ikusov.training.telegrambot.services.DonateMessageHardcodedProvider;
import ru.ikusov.training.telegrambot.services.DonateMessageProvider;

import java.util.Set;

@Component
@Order(250)
public class DonateCommandMessageHandler extends CommandMessageHandler {
    private final Set<String> commandVariants = Set.of("/donate", "/спонсор");
    private final DonateMessageProvider donateMessageProvider = new DonateMessageHardcodedProvider();

    @Override
    public BotReaction handleCommand(MyBotCommand command) {
        String msgText = donateMessageProvider.fetchMessage();
        return new BotMessageSender(command.getChatId(), msgText);
    }

    @Override
    protected void addHelp() {
        String help = commandVariants.stream().reduce((s1, s2) -> s1 + ", " + s2).orElse("");
        help += " - поддержать бота финансово.\n";
        helpString = help + helpString;
    }

    @Override
    protected Set<String> getCommandVariants() {
        return commandVariants;
    }
}
