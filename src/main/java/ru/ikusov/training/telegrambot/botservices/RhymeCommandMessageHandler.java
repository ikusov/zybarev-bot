package ru.ikusov.training.telegrambot.botservices;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import ru.ikusov.training.telegrambot.model.MyBotCommand;
import ru.ikusov.training.telegrambot.services.RhymeGetter;

import java.util.Set;

@Component
@Order(70)
public class RhymeCommandMessageHandler extends CommandMessageHandler {
    private final Set<String> commandVariants = Set.of("/rhyme", "/стишок", "/стих");

    @Override
    protected Set<String> getCommandVariants() {
        return commandVariants;
    }

    @Override
    protected void addHelp() {
        String help = commandVariants.stream().reduce((s1, s2) -> s1 + ", " + s2).orElse("");
        help += " - смищной стишок из интернета.\n";
        helpString = help + helpString;
    }

    @Override
    public BotReaction handleCommand(MyBotCommand command) {
//        if (!commandVariants.contains(command.getCommand().toLowerCase())) return null;
//
        RhymeGetter rhymeGetter;
        String textAnswer;

        try {
            rhymeGetter = new RhymeGetter();
            textAnswer = rhymeGetter.getRhyme();
        } catch (Exception e) {
            textAnswer = e.getMessage();
        }

        return new BotMessageSender(command.getChatId(), textAnswer);
    }
}
