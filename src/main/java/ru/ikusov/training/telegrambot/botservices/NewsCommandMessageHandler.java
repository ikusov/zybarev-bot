package ru.ikusov.training.telegrambot.botservices;

import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import ru.ikusov.training.telegrambot.model.MyBotCommand;
import ru.ikusov.training.telegrambot.services.NewsGetter;

import java.util.Set;

@Component
@Order(50)
public class NewsCommandMessageHandler extends CommandMessageHandler{
    private final Set<String> commandVariants = Set.of("/news", "/n", "/новость", "/н");

    @Override
    protected Set<String> getCommandVariants() {
        return commandVariants;
    }

    @Override
    protected void addHelp() {
        String help = commandVariants.stream().reduce((s1, s2) -> s1 + ", " + s2).orElse("");
        help += " - случайная новость из выдачи Яндекса.\n";
        helpString = help + helpString;
    }


    @Override
    public BotReaction handleCommand(MyBotCommand command) {
//        if (!commandVariants.contains(command.getCommand().toLowerCase())) return null;
//
        NewsGetter newsGetter;
        String textAnswer;

        try {
            newsGetter = new NewsGetter();
            textAnswer = newsGetter.getNews();
        } catch (Exception e) {
            textAnswer = e.getMessage();
        }

        return new BotMessageSender(command.getChatId(), command.getTopicId(), textAnswer);
    }
}
