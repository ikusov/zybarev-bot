package ru.ikusov.training.telegrambot.model.wordle;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ru.ikusov.training.telegrambot.model.CommonEntity;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "wordle_chat_current_word")
public class WordleChatCurrentWordEntity implements CommonEntity {
    @Id
    @Column(name = "chat_id")
    private Long chatId;
    @Column(name = "current_word")
    private String currentWord;

    @Column(name = "extended_chat_id")
    private String extendedChatId;
}
