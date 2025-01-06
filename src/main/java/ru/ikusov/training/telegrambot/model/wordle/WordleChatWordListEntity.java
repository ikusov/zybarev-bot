package ru.ikusov.training.telegrambot.model.wordle;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ru.ikusov.training.telegrambot.model.CommonEntity;

import java.util.List;

@Entity
@Table(name = "wordle_chat_word_list")
@Getter
@Setter
@NoArgsConstructor
public class WordleChatWordListEntity implements CommonEntity {
    @Id
    @Column(name = "chat_id")
    private Long chatId;
    @Column(name = "word_list")
    private List<String> wordList;

    public WordleChatWordListEntity(long chatId, List<String> wordList) {
        this.chatId = chatId;
        this.wordList = wordList;
    }
}
