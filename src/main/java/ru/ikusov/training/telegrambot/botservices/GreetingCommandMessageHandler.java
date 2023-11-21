package ru.ikusov.training.telegrambot.botservices;

import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import ru.ikusov.training.telegrambot.botservices.annotation.ExcludeFromHelp;
import ru.ikusov.training.telegrambot.model.MyBotCommand;
import ru.ikusov.training.telegrambot.utils.MessageType;

import java.util.Set;

@Component
@Order(30)
public class GreetingCommandMessageHandler extends CommandMessageHandler {

    @Override
    protected Set<String> getCommandVariants() {
        return Set.of("/bot", "/hello", "/пт", "/бот", "/привет");
    }

    @Override
    @ExcludeFromHelp
    protected String getHelpString() {
return "приветствие от бота";
}

    @Override
    public BotReaction handleCommand(MyBotCommand command) {
        String userName = command.getUser().getFirstName();

        String textAnswer =
//                RandomMessageGenerator.generate(RandomMessageGenerator.MessageType.GREETING_MESSAGE, userName);
                String.format(
                    MessageType.GREETING_MESSAGE.getRandomMessage(),
                    userName);

        return new BotMessageSender(command.getChatId(), command.getTopicId(), textAnswer);
    }
}
