package ru.ikusov.training.telegrambot.botservices;

import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import ru.ikusov.training.telegrambot.Bot;

/**
 * for sending non-formatted messages to chat
 */
public class BotMessageSender implements BotReaction {
    protected final SendMessage sendMessage;
    protected final String chatId;
    protected final Integer topicId;
    protected final String textMessage;

    public BotMessageSender(String chatId, Integer topicId, String textMessage) {
        this.chatId = chatId;
        this.topicId = topicId;
        this.textMessage = textMessage;
        this.sendMessage = SendMessage.builder()
                .chatId(chatId)
                .messageThreadId(topicId)
                .text(textMessage)
                .build();
    }

    @Override
    public void react(Bot bot) {
        try {
            bot.execute(sendMessage);
        } catch (TelegramApiException e) {
            log.warn("Telegram API error while sending message! {}", e.getMessage());
        }
    }

    @Override
    public void log() {
        log.info("REACTION ChatId: '{}' TopicId: '{}' TextMessage: {}", chatId, topicId, textMessage);
    }
}
