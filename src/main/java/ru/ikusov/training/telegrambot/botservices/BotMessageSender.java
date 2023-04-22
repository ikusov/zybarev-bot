package ru.ikusov.training.telegrambot.botservices;

import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import ru.ikusov.training.telegrambot.Bot;

/**
 * for sending non-formatted messages to chat
 */
public class BotMessageSender implements BotReaction {

    private final String chatId;
    private final String textMessage;

    public BotMessageSender(String chatId, String textMessage) {
        this.chatId = chatId;
        this.textMessage = textMessage;
    }

    @Override
    public void react(Bot bot) {
        try {
            SendMessage sendMessage = new SendMessage(chatId, textMessage);
            bot.execute(sendMessage);
        } catch (TelegramApiException e) {
            log.warn("Telegram API error while sending message! {}", e.getMessage());
        }
    }

    @Override
    public void log() {
        log.info("REACTION ChatId: '{}' TextMessage: {}", chatId, textMessage);
    }
}
