package ru.ikusov.training.telegrambot.botservices;

import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import ru.ikusov.training.telegrambot.Bot;

public class BotMessageSender implements BotReaction {

    private String chatId;
    private String textMessage;

    public BotMessageSender(String chatId, String textMessage) {
        this.chatId = chatId;
        this.textMessage = textMessage;
    }

    @Override
    public void react(Bot bot) {
        try {
            SendMessage sendMessage = new SendMessage(chatId, textMessage);
//            sendMessage.setParseMode("MarkdownV2");
            bot.execute(sendMessage);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }
}
