package ru.ikusov.training.telegrambot.botservices;

import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import ru.ikusov.training.telegrambot.model.MyBotCommand;

import java.util.Set;

@Component
@Order(190)
public class BeeCommandMessageHandler extends CommandMessageHandler {
    private final Set<String> commandVariants = Set.of("/bee", "/пчела");

    @Override
    public BotReaction handleCommand(MyBotCommand command) {
        return new BotFormattedMessageSender(
                command.getChatId(),
                command.getTopicId(),
                """
                        \uD83D\uDC1D
                        Ты пчела я пчеловод
                        А мы любим мёд
                        А мне повезёт
                        С тобой мне повезёт"""
        );
    }

    @Override
    protected void addHelp() {
        String help = commandVariants.stream().reduce((s1, s2) -> s1 + ", " + s2).orElse("");
        help += " - пчела.\n";
        helpString = help + helpString;
    }

    @Override
    protected Set<String> getCommandVariants() {
        return commandVariants;
    }
}
