package ru.ikusov.training.telegrambot.services.wordle;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import ru.ikusov.training.telegrambot.model.WordEntity;
import ru.ikusov.training.telegrambot.services.DatabaseConnector;

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
        WordStatus ws = WordStatus.USED;
        WordEntity result;
        do {
            result = databaseConnector.getById(WordEntity.class, (int)(Math.random()*100));
            ws = WordStatus.getByStatus(result.getStatus());
        } while(ws == WordStatus.USED);

        return result.getText();
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
