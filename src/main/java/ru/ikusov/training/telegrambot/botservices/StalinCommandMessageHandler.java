package ru.ikusov.training.telegrambot.botservices;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import ru.ikusov.training.telegrambot.model.MyBotCommand;
import ru.ikusov.training.telegrambot.services.RhymeGetter;
import ru.ikusov.training.telegrambot.services.StalinImageGetter;

import java.util.Set;

@Component
@Order(130)
public class StalinCommandMessageHandler extends CommandMessageHandler {
    private final Set<String> commandVariants = Set.of("/stalin", "/сталин");

    @Autowired
    private StalinImageGetter stalinImageGetter;

    @Override
    protected Set<String> getCommandVariants() {
        return commandVariants;
    }

    @Override
    public BotReaction handleCommand(MyBotCommand command) {
        if (!commandVariants.contains(command.getCommand().toLowerCase())) return null;

        String photoAnswer;

        try {
            photoAnswer = stalinImageGetter.getStalinImage();
        } catch (Exception e) {
            photoAnswer = e.getMessage();
            return new BotMessageSender(command.getChatId(), photoAnswer);
        }

        return new BotPhotoSender(command.getChatId(), photoAnswer);
    }

}
