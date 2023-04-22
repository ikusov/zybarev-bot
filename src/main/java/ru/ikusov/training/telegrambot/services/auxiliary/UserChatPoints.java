package ru.ikusov.training.telegrambot.services.auxiliary;

import java.util.Objects;

public class UserChatPoints {
    private final Long chatId;
    private final Long userId;

    public UserChatPoints(Long chatId, Long userId) {
        this.chatId = chatId;
        this.userId = userId;
    }

    public Long getChatId() {
        return chatId;
    }

    public Long getUserId() {
        return userId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        UserChatPoints that = (UserChatPoints) o;

        if (!Objects.equals(chatId, that.chatId)) return false;
        return Objects.equals(userId, that.userId);
    }

    @Override
    public int hashCode() {
        int result = chatId != null ? chatId.hashCode() : 0;
        result = 31 * result + (userId != null ? userId.hashCode() : 0);
        return result;
    }
}
