package ru.ikusov.training.telegrambot.botservices;

import org.springframework.beans.factory.annotation.Autowired;
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

        return new BotMessageSender(command.getChatId(), textAnswer);
    }
}
