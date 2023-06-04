package ru.ikusov.training.telegrambot.botservices;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.telegram.telegrambots.meta.api.objects.Message;
import ru.ikusov.training.telegrambot.Bot;
import ru.ikusov.training.telegrambot.services.UserNameGetter;

public abstract class NonCommandMessageHandler implements MessageHandler {
    protected final Logger log = LoggerFactory.getLogger(this.getClass());

    protected Bot bot;

    @Autowired
    public void setBot(Bot bot) {
        this.bot = bot;
    }

    @Override
    public void handleMessage(Message message) {
        BotReaction botReaction = handleNonCommand(message);
        botReaction.react(bot);
        botReaction.log();
    }

    public abstract BotReaction handleNonCommand(Message message);

    protected void log(Message message) {
        log.info(
                "NON COMMAND ChatId: '{}' TopicId: '{}' ChatName: '{}' UserId: '{}' UserName:'{}' Text: {}",
                message.getChatId(),
                message.getMessageThreadId(),
                message.getChat().getTitle(),
                message.getFrom().getId(),
                UserNameGetter.getUserName(message.getFrom()),
                message.getText()
        );
    }
}
