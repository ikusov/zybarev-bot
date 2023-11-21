package ru.ikusov.training.telegrambot.botservices;

import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import ru.ikusov.training.telegrambot.botservices.annotation.ExcludeFromHelp;
import ru.ikusov.training.telegrambot.model.MyBotCommand;
import ru.ikusov.training.telegrambot.services.RhymeGetter;

import java.util.Set;

@Component
@Order(70)
public class RhymeCommandMessageHandler extends CommandMessageHandler {

    @Override
    protected Set<String> getCommandVariants() {
        return Set.of("/rhyme", "/стишок", "/стих");
    }

    @Override
    @ExcludeFromHelp
    protected String getHelpString() {
        return "смищной стишок из интернета";
    }

    @Override
    public BotReaction handleCommand(MyBotCommand command) {
        RhymeGetter rhymeGetter;
        String textAnswer;

        try {
            rhymeGetter = new RhymeGetter();
            textAnswer = rhymeGetter.getRhyme();
        } catch (Exception e) {
            textAnswer = e.getMessage();
        }

        return new BotMessageSender(command.getChatId(), command.getTopicId(), textAnswer);
    }
}
