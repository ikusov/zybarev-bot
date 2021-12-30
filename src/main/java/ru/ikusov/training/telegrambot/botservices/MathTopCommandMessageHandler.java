package ru.ikusov.training.telegrambot.botservices;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import ru.ikusov.training.telegrambot.model.MyBotCommand;
import ru.ikusov.training.telegrambot.services.MathTopGetter;
import ru.ikusov.training.telegrambot.services.RhymeGetter;
import ru.ikusov.training.telegrambot.services.UserNameGetter;

import java.util.Set;

@Component
@Order(150)
public class MathTopCommandMessageHandler extends CommandMessageHandler {
    private final Set<String> commandVariants = Set.of("/top", "/топ");

    @Autowired
    MathTopGetter mathTopGetter;

    @Override
    protected Set<String> getCommandVariants() {
        return commandVariants;
    }

    @Override
    protected void addHelp() {
        String help = commandVariants.stream().reduce((s1, s2) -> s1 + ", " + s2).orElse("");
        help += " - топ математических баллов.\n";
        helpString = help + helpString;
    }

    @Override
    public BotReaction handleCommand(MyBotCommand command) {
//        if (!commandVariants.contains(command.getCommand().toLowerCase())) return null;
//
        String textAnswer = "";

        try {
            var mathTop = mathTopGetter.getMathTop(command.getChatId());
            for (int i=0; i<mathTop.size(); i++) {
                textAnswer += String.format("%d. %s - %d баллов\n",
                        i+1,
                        mathTop.get(i).getKey(),
                        mathTop.get(i).getValue());
            }
        } catch (Exception e) {
            textAnswer = e.getMessage();
        }

        return new BotMessageSender(command.getChatId(), textAnswer);
    }

}
