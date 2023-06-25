package ru.ikusov.training.telegrambot.dao.wordle;

import ru.ikusov.training.telegrambot.model.WordleEventDto;
import ru.ikusov.training.telegrambot.model.wordle.WordleUserEntity;

import java.util.List;

public interface WordleRepository {
    long TIME_EXPIRING_WORD_ATTEMPT_SECONDS = 24 * 3600;
    int WORD_INDEXES_ARRAY_PART_SIZE = 300;
    String getCurrentWordForChat(Long chatId);

    List<String> getTriedWordsForChat(Long chatId);

    boolean isAnyWordAttempts(Long chatId);

    /**
     * Gets remaining word attempts count for user in chat
     *
     * @param userId user id
     * @param chatId chat id
     * @param currentAllowedAttempts max allowed word attempts for current word
     * @return remaining word attempts count
     */
    int getWordAttemptsCount(Long userId, Long chatId, int currentAllowedAttempts);

    long getWordAttemptTTL(Long userId, Long chatId);

    void saveWordAttempt(String word, Long chatId);

    void decreaseAttemptsCount(Long userId, Long chatId);

    void setRightAnswer(Long chatId);

    String getNextRandomWordForChat(Long chatId, int wordLen);

    void saveWordleEvent(WordleEventDto we);

    /**
     * Adding points for user and persisting them to repository
     * @param chatId telegram chat id
     * @param userId telegram user id
     * @param points points for adding
     * @return sum of points for user in chat
     */
    int addPointsForUser(Long chatId, Long userId, int points);

    WordleUserEntity fetchWordleUserEntity(Long userId, Long chatId);

    int getPointsForUserInChat(Long chatId, Long userId);

    List<WordleUserEntity> getWordleUsersForChat(Long chatId);
}
