package ru.ikusov.training.telegrambot.botservices;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.ikusov.training.telegrambot.model.CommandType;
import ru.ikusov.training.telegrambot.model.MyBotCommand;
import ru.ikusov.training.telegrambot.services.GreetingService;

@Component
@RequiredArgsConstructor
public class GreetingCommandMessageHandler extends CommandMessageHandler {
    private final GreetingService greetingService;

    @Override
    protected CommandType getSupportedCommandType() {
        return CommandType.GREETING;
    }

    @Override
    public BotReaction handleCommand(MyBotCommand command) {
        String userName = command.user().getFirstName();

        String textAnswer =
                String.format(
                    greetingService.greetingMessage(),
                    userName);

        return new BotMessageSender(command.chatId(), command.topicId(), textAnswer);
    }
}
