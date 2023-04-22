package ru.ikusov.training.telegrambot.model.wordle;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import ru.ikusov.training.telegrambot.model.CommonEntity;

import java.util.List;

@Entity
@Table(name = "wordle_chat_attempt_list")
public class WordleChatAttemptListEntity implements CommonEntity {
    public Long getChatId() {
        return chatId;
    }

    public void setChatId(Long chatId) {
        this.chatId = chatId;
    }

    public List<String> getAttemptList() {
        return attemptList;
    }

    public void setAttemptList(List<String> attemptList) {
        this.attemptList = attemptList;
    }

    public WordleChatAttemptListEntity(Long chatId, List<String> attemptList) {
        this.chatId = chatId;
        this.attemptList = attemptList;
    }

    public WordleChatAttemptListEntity() {
    }

    @Id
    @Column(name = "chat_id")
    private Long chatId;
    @Column(name = "attempt_list")
    private List<String> attemptList;
}
