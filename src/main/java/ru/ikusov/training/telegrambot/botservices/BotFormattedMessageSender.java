package ru.ikusov.training.telegrambot.botservices;

import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import ru.ikusov.training.telegrambot.Bot;

/**
 * class for sending formatted (markdownv2) message to chat
 * may be excess and its functionality could be added to BotMessageSender
 */
public class BotFormattedMessageSender implements BotReaction {

    private final String chatId;
    private final String textMessage;

    public BotFormattedMessageSender(String chatId, String textMessage) {
        this.chatId = chatId;
        this.textMessage = textMessage;
    }

    @Override
    public void react(Bot bot) {
        try {
            SendMessage sendMessage = new SendMessage(chatId, textMessage);
            sendMessage.setParseMode("MarkdownV2");
            bot.execute(sendMessage);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }
}
