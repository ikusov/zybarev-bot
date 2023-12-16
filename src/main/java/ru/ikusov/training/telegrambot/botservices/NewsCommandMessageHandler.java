package ru.ikusov.training.telegrambot.botservices;

import org.springframework.stereotype.Component;
import ru.ikusov.training.telegrambot.model.CommandType;
import ru.ikusov.training.telegrambot.model.MyBotCommand;
import ru.ikusov.training.telegrambot.services.NewsGetter;

@Component
public class NewsCommandMessageHandler extends CommandMessageHandler{

    @Override
    protected CommandType getSupportedCommandType() {
        return CommandType.NEWS;
    }

    @Override
    public BotReaction handleCommand(MyBotCommand command) {
        NewsGetter newsGetter;
        String textAnswer;

        try {
            newsGetter = new NewsGetter();
            textAnswer = newsGetter.getNews();
        } catch (Exception e) {
            textAnswer = e.getMessage();
        }

        return new BotMessageSender(command.chatId(), command.topicId(), textAnswer);
    }
}
