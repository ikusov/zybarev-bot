package ru.ikusov.training.telegrambot.botservices;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import ru.ikusov.training.telegrambot.model.MyBotCommand;
import ru.ikusov.training.telegrambot.services.wordle.WordleService;

import java.util.Set;

@Component
@Order(190)
public class BeeCommandMessageHandler extends CommandMessageHandler {
    private final Set<String> commandVariants = Set.of("/bee", "/пчела");

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

    @Override
    public BotReaction handleCommand(MyBotCommand command) {
        return new BotFormattedMessageSender(
                command.getChatId(),
                "\uD83D\uDC1D" +
                        "\nТы пчела я пчеловод" +
                        "\nА мы любим мёд" +
                        "\nА мне повезёт" +
                        "\nС тобой мне повезёт"
        );
    }
}
