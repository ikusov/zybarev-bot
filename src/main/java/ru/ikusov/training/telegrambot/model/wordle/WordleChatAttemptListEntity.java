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

import java.util.List;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "wordle_chat_attempt_list")
public class WordleChatAttemptListEntity implements CommonEntity {
    @Id
    @Column(name = "chat_id")
    private Long chatId;

    @Column(name = "attempt_list")
    private List<String> attemptList;
}
