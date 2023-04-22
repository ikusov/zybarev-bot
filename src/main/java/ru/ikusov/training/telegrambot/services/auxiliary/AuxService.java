package ru.ikusov.training.telegrambot.services.auxiliary;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import ru.ikusov.training.telegrambot.dao.DatabaseConnector;
import ru.ikusov.training.telegrambot.dao.wordle.WordleRepository;
import ru.ikusov.training.telegrambot.model.ChatEntity;
import ru.ikusov.training.telegrambot.model.UserEntity;
import ru.ikusov.training.telegrambot.model.wordle.WordleEventEntity;
import ru.ikusov.training.telegrambot.services.UserNameGetter;
import ru.ikusov.training.telegrambot.services.wordle.WordlePointsCalculator;

import java.util.*;

/**
 * Вспомогательная логика для всяких админских разовых действий.
 */
@Component
public class AuxService {
    private final Logger log = LoggerFactory.getLogger(this.getClass());
    private final DatabaseConnector databaseConnector;
    private final WordleRepository wordleRepository;

    @Autowired
    public AuxService(DatabaseConnector databaseConnector, WordleRepository wordleRepository) {
        this.databaseConnector = databaseConnector;
        this.wordleRepository = wordleRepository;
    }

    public String showPointsForSomeUsers() {
        var userChatPoints = getUserChatPoints();
        return getPrettyString(userChatPoints);
    }

    public String calculateAndSavePoints() {
        var userChatPoints = getUserChatPoints();
        var dbEntriesCount = saveChatPoints(userChatPoints);
        return getPrettyString(userChatPoints) + "\n\n" + dbEntriesCount + " записей сохранено в БД!";
    }

    private int saveChatPoints(Map<UserChatPoints, Integer> userChatPoints) {
        int count = 0;
        for (var userChat : userChatPoints.keySet()) {
            var userId = userChat.getUserId();
            var chatId = userChat.getChatId();
            try {
                var wue = wordleRepository.fetchWordleUserEntity(userId, chatId);
                wue.setPoints(userChatPoints.get(userChat).longValue());
                databaseConnector.saveOrUpdate(wue);
                count++;
            } catch (Exception e) {
                log.error("Error while saving points to DB for userId {} chatId {}", userId, chatId);
            }
        }

        return count;
    }

    private Map<UserChatPoints, Integer> getUserChatPoints() {
        Map<Long, List<String>> chatWordAttempts = new HashMap<>();
        Map<UserChatPoints, Integer> userChatPoints = new HashMap<>();

        var eventList = databaseConnector.getByQuery(WordleEventEntity.class, "from WordleEventEntity");
        log.info("Получено из таблицы wordle_entity {} записей!", eventList.size());

        eventList.sort(Comparator.comparing(WordleEventEntity::getTimestamp));
        for (var event : eventList) {
            var chat = event.getChatId();
            var userChat = new UserChatPoints(event.getChatId(), event.getUserId());
            var attemptWord = event.getAttemptWord();
            if (attemptWord == null) continue;

            chatWordAttempts.computeIfAbsent(chat, e -> new ArrayList<>()).add(attemptWord);
            if (event.isRight()) {
                var attempts = chatWordAttempts.get(chat);
                int points = WordlePointsCalculator.countPoints(attempts);
                int userSumPoints = userChatPoints.getOrDefault(userChat, 0);
                userChatPoints.put(userChat, userSumPoints + points);

                chatWordAttempts.put(chat, new ArrayList<>());
            }
        }
        log.info("Получены данные для {} чатов и {} пользователей-в-чатах!",
                chatWordAttempts.size(), userChatPoints.size());
        return userChatPoints;
    }

    private String getPrettyString(Map<UserChatPoints, Integer> userChatPoints) {
        List<String> userChatList = new ArrayList<>();
        for (var userChat : userChatPoints.keySet()) {
            String userName = UserNameGetter.getUserName(
                    databaseConnector.getById(UserEntity.class, userChat.getUserId()).orElse(new UserEntity())
            );
            String chatName = databaseConnector.getById(ChatEntity.class, userChat.getChatId())
                    .orElse(new ChatEntity())
                    .getTitle();
            int points = userChatPoints.get(userChat);
            String prettyMsg = userName + " в \"" + chatName + "\" pts: " + points + "\n";
            userChatList.add(prettyMsg);
        }

        StringBuilder usersResults = new StringBuilder(userChatList.size());
        userChatList.forEach(usersResults::append);

        return usersResults.toString();
    }
}
