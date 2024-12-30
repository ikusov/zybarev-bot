package ru.ikusov.training.telegrambot.botservices;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.ikusov.training.telegrambot.botservices.annotation.ExcludeFromHelp;
import ru.ikusov.training.telegrambot.model.CommandType;
import ru.ikusov.training.telegrambot.model.MyBotCommand;

import java.util.Arrays;

@Slf4j
@Component
public class HelpCommandMessageHandler extends CommandMessageHandler {
    @Override
    public BotReaction handleCommand(MyBotCommand command) {
        String textAnswer = Arrays.stream(CommandType.values())
                .filter(c -> !(isExcludedFromHelp(c) || c.getHelpString().isEmpty()))
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

    private static boolean isExcludedFromHelp(CommandType command) {
        try {
            return command.getClass().getField(command.name()).isAnnotationPresent(ExcludeFromHelp.class);
        } catch (NoSuchFieldException e) {
            log.error("Not found CommandType == '{}'", command.name());
            return false;
        }
    }
}
