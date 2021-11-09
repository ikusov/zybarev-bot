package ru.ikusov.training.telegrambot.botservices;

import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;
import org.telegram.telegrambots.meta.api.objects.InputFile;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import ru.ikusov.training.telegrambot.Bot;

public class BotPhotoSender implements BotReaction {

    private String chatId;
    private String photoURL;

    public BotPhotoSender(String chatId, String photoURL) {
        this.chatId = chatId;
        this.photoURL = photoURL;
    }

    @Override
    public void react(Bot bot) {
        try {
            bot.execute(new SendPhoto(chatId, new InputFile(photoURL)));
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }
}
