package ru.ikusov.training.telegrambot.botservices;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Message;
import ru.ikusov.training.telegrambot.Bot;

public abstract class NonCommandMessageHandler implements MessageHandler {
    protected Bot bot;

    @Autowired
    public void setBot(Bot bot) {
        this.bot = bot;
    }

    @Override
    public void handleMessage(Message message) {
//        System.out.println(this.getClass().getSimpleName() + ".handleMessage()!");

        BotReaction botReaction = handleNonCommand(message);
        if (botReaction != null)
            botReaction.react(bot);
    }

    public abstract BotReaction handleNonCommand(Message message);
}
