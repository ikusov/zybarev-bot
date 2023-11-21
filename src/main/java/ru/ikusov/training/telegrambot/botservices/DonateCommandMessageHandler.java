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
    private final DonateMessageProvider donateMessageProvider = new DonateMessageHardcodedProvider();

    @Override
    protected Set<String> getCommandVariants() {
        return Set.of("/donate", "/спонсор");
    }

    @Override
    protected String getHelpString() {
        return "поддержать бота финансово";
    }

    @Override
    public BotReaction handleCommand(MyBotCommand command) {
        String msgText = donateMessageProvider.fetchMessage();
        return new BotMessageSender(command.getChatId(), command.getTopicId(), msgText);
    }

}
