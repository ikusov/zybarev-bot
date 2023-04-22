package ru.ikusov.training.telegrambot.model.wordle;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import ru.ikusov.training.telegrambot.model.CommonEntity;

import java.util.List;

@Entity
@Table(name = "wordle_chat_word_list")
public class WordleChatWordListEntity implements CommonEntity {
    @Id
    @Column(name = "chat_id")
    private Long chatId;
    @Column(name = "word_list")
    private List<String> wordList;

    public long getChatId() {
        return chatId;
    }

    public void setChatId(long chatId) {
        this.chatId = chatId;
    }

    public List<String> getWordList() {
        return wordList;
    }

    public void setWordList(List<String> wordList) {
        this.wordList = wordList;
    }

    public WordleChatWordListEntity(long chatId, List<String> wordList) {
        this.chatId = chatId;
        this.wordList = wordList;
    }

    public WordleChatWordListEntity() {
    }
}
