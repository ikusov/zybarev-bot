package ru.ikusov.training.telegrambot.botservices.utils;

import lombok.experimental.UtilityClass;

@UtilityClass
public class TelegramChatUtils {
    public static String buildExtendedChatId(Long chatId, Integer topicId) {
        return chatId + "_" + topicId;
    }
}
