package ru.ikusov.training.telegrambot.botservices;

import org.telegram.telegrambots.meta.api.methods.groupadministration.LeaveChat;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import ru.ikusov.training.telegrambot.Bot;

public class BotChatLeaver implements BotReaction {
    private final String chatId;

    public BotChatLeaver(String chatId) {
        this.chatId = chatId;
    }

    @Override
    public void react(Bot bot) {
        try {
            bot.execute(new LeaveChat(chatId));
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }
}
