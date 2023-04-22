package ru.ikusov.training.telegrambot.dao.wordle;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;
import ru.ikusov.training.telegrambot.dao.DatabaseConnector;
import ru.ikusov.training.telegrambot.model.WordleEventDto;
import ru.ikusov.training.telegrambot.model.wordle.WordleEventEntity;

import java.time.Instant;
import java.util.List;

@Component
@Primary
public class WordleEventRepository {
    private final Logger log = LoggerFactory.getLogger(this.getClass());
    private final DatabaseConnector databaseConnector;

    public WordleEventRepository(DatabaseConnector databaseConnector) {
        this.databaseConnector = databaseConnector;
    }

    public void saveWordleEvent(WordleEventDto we) {
        WordleEventEntity wordleEventEntity = new WordleEventEntity(
                Instant.now().getEpochSecond(),
                we.getChatId(),
                we.getUserId(),
                we.getCurrentWord(),
                we.getAttemptWord(),
                we.isRight()
        );
        databaseConnector.save(wordleEventEntity);
    }

    public List<WordleEventEntity> getEventsForUser(Long userId, Long chatId) {
        return
                databaseConnector.getByQuery(WordleEventEntity.class, "from WordleEventEntity " +
                        "where userId=" + userId +
                        " and chatId=" + chatId);
    }

    public List<WordleEventEntity> getEventsForChat(Long chatId) {
        return
                databaseConnector.getByQuery(WordleEventEntity.class, "from WordleEventEntity " +
                        "where chatId=" + chatId);
    }

    public Long countMadeWords() {
        return databaseConnector.getCountByQuery(Long.class,
                "select count(*) from WordleEventEntity where attemptWord is null").get(0);
    }

    public Long countAttempts() {
        return databaseConnector.getCountByQuery(Long.class,
                "select count(*) from WordleEventEntity where attemptWord is not null").get(0);
    }

    public Long getOldestEventTimestamp() {
        var result =
                databaseConnector.getCountByQuery(Long.class,
                        "select min(timestamp) from WordleEventEntity").get(0);
        return result != null ? result : Instant.now().getEpochSecond();
    }
}
