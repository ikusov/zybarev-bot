package ru.ikusov.training.telegrambot.botservices;

import org.springframework.stereotype.Component;
import ru.ikusov.training.telegrambot.model.CommandType;
import ru.ikusov.training.telegrambot.model.MyBotCommand;

import java.util.Arrays;

@Component
public class HelpCommandMessageHandler extends CommandMessageHandler {
    @Override
    public BotReaction handleCommand(MyBotCommand command) {
        String textAnswer = Arrays.stream(CommandType.values())
                .filter(c -> !"".equals(c.getHelpString()))
                .map(c -> c.getAliases().stream()
                        .map(a -> "/" + a)
                        .reduce((a1, a2) -> a1 + ", " + a2).orElse("")
                        + " - "
                        + c.getHelpString()
                )
                .reduce((s1, s2) -> s1 + "\n" + s2)
                .orElse("");

        return new BotMessageSender(command.chatId(), command.topicId(), textAnswer);
    }

    @Override
    protected CommandType getSupportedCommandType() {
        return CommandType.HELP;
    }
}
