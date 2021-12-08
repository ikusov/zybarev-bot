package ru.ikusov.training.telegrambot.botservices;

import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import ru.ikusov.training.telegrambot.model.MyBotCommand;
import ru.ikusov.training.telegrambot.utils.MessageType;
import ru.ikusov.training.telegrambot.utils.RandomMessageGenerator;

import java.util.Set;

@Component
@Order
public class UnknownCommandMessageHandler extends CommandMessageHandler{
    private final Set<String> commandVariants = Set.of();

    @Override
    protected Set<String> getCommandVariants() {
        return commandVariants;
    }

    @Override
    public BotReaction handleCommand(MyBotCommand command) {
        if(registeredCommands.contains(command.getCommand())) return new BotEmptyReaction();

        String textAnswer =
//                RandomMessageGenerator
//                .generate(RandomMessageGenerator.MessageType.UNKNOWN_COMMAND_MESSAGE,
//                        command.getCommand());
                String.format(
                        MessageType.UNKNOWN_COMMAND_MESSAGE.getRandomMessage(),
                        command.getCommand());
        return new BotMessageSender(command.getChatId(), textAnswer);
    }
}
