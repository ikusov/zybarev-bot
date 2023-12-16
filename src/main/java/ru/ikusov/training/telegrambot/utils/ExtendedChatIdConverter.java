package ru.ikusov.training.telegrambot.utils;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class ExtendedChatIdConverter {
    private static final String DELIMITER = "_";

    public static String toExtendedChatId(Long chatId, Integer messageThreadId) {
        return chatId + (
                messageThreadId == null
                        ? ""
                        : DELIMITER + messageThreadId
        );
    }
}
