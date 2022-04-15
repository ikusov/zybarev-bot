package ru.ikusov.training.telegrambot.services.wordle;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import ru.ikusov.training.telegrambot.model.WordEntity;
import ru.ikusov.training.telegrambot.services.DatabaseConnector;

import java.util.Optional;

@Component
public class WordleRepository {
    private DatabaseConnector databaseConnector;

    @Autowired
    public WordleRepository(DatabaseConnector databaseConnector) {
        this.databaseConnector = databaseConnector;
    }

    public boolean isWordInDB(String word) {
        var result = databaseConnector.getByQuery(WordEntity.class, "from WordEntity where text='" + word + "'");
        return result.isEmpty();
    }

    public String getRandomWord() {
        WordEntity result;
        do {
            result = databaseConnector.getById(WordEntity.class, (int)(Math.random()*726)+1);
        } while(result == null || WordStatus.getByStatus(result.getStatus()) == WordStatus.USED);

        result.setTimeStamp(System.currentTimeMillis()/1000);
        result.setStatus(WordStatus.IN_USE.getStatus());
        databaseConnector.save(result);

        return result.getText();
    }

    public Optional<WordEntity> getCurrentWord() {
        var we = databaseConnector
                .getByQuery(WordEntity.class,
                        "from WordEntity where status=" + WordStatus.IN_USE.getStatus());
        return we.isEmpty()
                ? Optional.empty()
                : Optional.of(we.get(0));
    }

    public Optional<WordEntity> getLastTriedWord() {
        var we = databaseConnector
                .getByQuery(WordEntity.class,
                        "from WordEntity where (status is null or status=" +
                                WordStatus.NOT_USED.getStatus() +
                                ") and timestamp=(select max(timestamp) from WordEntity)"
                        );
        return we.isEmpty()
                ? Optional.empty()
                : Optional.of(we.get(0));
    }

    public Optional<WordEntity> getLastGuessedWord() {
        var we = databaseConnector
                .getByQuery(WordEntity.class,
                        "from WordEntity where status=" +
                                WordStatus.USED.getStatus() +
                                " and timestamp=(select max(timestamp) from WordEntity)"
                );
        return we.isEmpty()
                ? Optional.empty()
                : Optional.of(we.get(0));
    }

    public void setLastGuessedWord(WordEntity we) {
        databaseConnector.save(we);
    }

    public void setLastTriedWord(WordEntity we) {
        databaseConnector.save(we);
    }

    public Optional<WordEntity> getWordByText(String word) {
        var we = databaseConnector
                .getByQuery(WordEntity.class,
                        "from WordEntity where text='" +
                                word + "'"
                );
        return we.isEmpty()
                ? Optional.empty()
                : Optional.of(we.get(0));
    }

    private enum WordStatus {
        NOT_USED(0),
        IN_USE(1),
        USED(2);

        private final int status;

        WordStatus(int status) {
            this.status = status;
        }

        public int getStatus() {
            return status;
        }

        public static WordStatus getByStatus(int status) {
            for (var ws : WordStatus.values()) {
                if (ws.getStatus() == status) {
                    return ws;
                }
            }
            return NOT_USED;
        }
    }
}
