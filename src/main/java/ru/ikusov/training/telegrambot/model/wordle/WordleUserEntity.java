package ru.ikusov.training.telegrambot.model.wordle;

import jakarta.persistence.*;
import ru.ikusov.training.telegrambot.model.CommonEntity;

/**
 * Эта сущность пользователь-в-игре, т.е. такой пользователь, который загадал или пытался отгадывать слово.
 * У одного и того же пользователя телеграм может быть несколько таких сущностей (т.к. в каждом чате своя
 * игра).
 */
@Entity
@Table(name = "wordle_user")
public class WordleUserEntity implements CommonEntity {
    /**
     * Генерируемый айдишник (для корректной работы хибера)
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    /**
     * Айди пользователя в телеге.
     */
    @Column(name = "user_id")
    Long userId;

    /**
     * Айди чата в телеге.
     */
    @Column(name = "chat_id")
    Long chatId;

    /**
     * Количество оставшихся попыток угадывания слова.
     */
    @Column(name = "attempts_count")
    Integer attemptsCount;

    /**
     * Момент первой попытки угадывания слова.
     */
    @Column(name = "first_attempt_timestamp_seconds")
    Long firstAttemptTimestampSeconds;

    /**
     * Количество очков у пользователя в чате.
     */
    @Column(name = "points")
    Long points;

    public WordleUserEntity(
            Long userId,
            Long chatId,
            Integer attemptsCount,
            Long firstAttemptTimestampSeconds
    ) {
        this.userId = userId;
        this.chatId = chatId;
        this.attemptsCount = attemptsCount;
        this.firstAttemptTimestampSeconds = firstAttemptTimestampSeconds;
        this.points = 0L;
    }

    public WordleUserEntity() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Long getChatId() {
        return chatId;
    }

    public void setChatId(Long chatId) {
        this.chatId = chatId;
    }

    public Integer getAttemptsCount() {
        return attemptsCount;
    }

    public void setAttemptsCount(Integer attemptsCount) {
        this.attemptsCount = attemptsCount;
    }

    public Long getFirstAttemptTimestampSeconds() {
        return firstAttemptTimestampSeconds;
    }

    public void setFirstAttemptTimestampSeconds(Long firstAttemptTimestampSeconds) {
        this.firstAttemptTimestampSeconds = firstAttemptTimestampSeconds;
    }
    public Long getPoints() {
        return points == null ? 0L : points;
    }

    public void setPoints(Long points) {
        this.points = points;
    }
}
