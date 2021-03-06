package ru.ikusov.training.telegrambot.services.wordle;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.User;
import ru.ikusov.training.telegrambot.model.WordAttempt;
import ru.ikusov.training.telegrambot.model.WordEntity;
import ru.ikusov.training.telegrambot.model.WordEntity2;
import ru.ikusov.training.telegrambot.model.WordsHistory;
import ru.ikusov.training.telegrambot.repository.DatabaseConnector;

import java.util.List;
import java.util.Optional;

@Component
public class WordleRepository {
    private final int MIN_WORD_ID = 1;
    private final int MAX_WORD_ID = 333;
    private final DatabaseConnector databaseConnector;

    @Autowired
    public WordleRepository(DatabaseConnector databaseConnector) {
        this.databaseConnector = databaseConnector;
    }

    public boolean isWordInDB(String word) {
        var result = databaseConnector.getByQuery(WordEntity.class, "from WordEntity where text='" + word + "'");
        return result.isEmpty();
    }

    public String getRandomWord() {
        WordEntity result = new WordEntity();
        int resultStatus = 0;

        do {
            long randomId = (long) (Math.random() * MAX_WORD_ID + MIN_WORD_ID);
            System.out.println("Try to get word from database with id = " + randomId);
            var resultOpt = databaseConnector.getById(WordEntity.class, randomId);
            if (resultOpt.isEmpty()) {
                continue;
            }
            result = resultOpt.get();
            System.out.println("Getted word entity from db: " + result);

            resultStatus = result.getStatus() == null ? 0 : result.getStatus();

        } while (WordStatus.getByStatus(resultStatus) == WordStatus.USED);

        result.setTimeStamp(System.currentTimeMillis() / 1000);
        result.setStatus(WordStatus.IN_USE.getStatus());
        System.out.println("Updated the word entity to: " + result);

        databaseConnector.saveOrUpdate(result);
        System.out.println("The word " + result + " is successfully resaved to da ta base!");

        return result.getText();
    }

    public Optional<WordEntity> getCurrentWord() {
        List<WordEntity> we = List.of();
        try {
            we = databaseConnector
                    .getByQuery(WordEntity.class,
                            "from WordEntity where status=" + WordStatus.IN_USE.getStatus());
        } catch (Exception e) {
            System.err.println("WordleRepository.getCurrentWord exception " + e.getMessage());
        }
        return we.isEmpty()
                ? Optional.empty()
                : Optional.of(we.get(0));
    }

    public Optional<WordEntity> getLastTriedWord() {
        List<WordEntity> we;
        WordEntity lastTried = null;
        try {
            we = databaseConnector
                    .getByQuery(
                            WordEntity.class,
                            "from WordEntity where timestamp is not null"
                    );
            long maxCT = 0;
            for (var w : we) {
                var status = w.getStatus();
                if ((status == null || status == WordStatus.NOT_USED.getStatus())
                        && w.getTimeStamp() > maxCT) {
                    maxCT = w.getTimeStamp();
                    lastTried = w;
                }
            }
        } catch (Exception e) {
            System.err.println("Error while get last tried word: " + e.getMessage());
        }
        return Optional.ofNullable(lastTried);
    }

    public Optional<WordEntity> getLastGuessedWord() {
        List<WordEntity> we;
        WordEntity lastGuessed = null;
        try {
            we = databaseConnector
                    .getByQuery(
                            WordEntity.class,
                            "from WordEntity where status=" +
                                    WordStatus.USED.getStatus()
                    );
            long maxCT = 0;
            for (var w : we) {
                if (w.getTimeStamp() > maxCT) {
                    maxCT = w.getTimeStamp();
                    lastGuessed = w;
                }
            }
        } catch (Exception e) {
            System.err.println("Exception while wordleRepository.getLastGuessedWord: "
                    + e.getMessage());
        }
        return Optional.ofNullable(lastGuessed);
    }

    public void setLastGuessedWord(WordEntity we) {
        we.setTimeStamp(System.currentTimeMillis() / 1000);
        we.setStatus(WordStatus.USED.getStatus());
        databaseConnector.saveOrUpdate(we);
    }

    public void setLastTriedWord(WordEntity we) {
        we.setTimeStamp(System.currentTimeMillis() / 1000);

        try {
            System.out.println("Trying to save tried word: " + we);
            databaseConnector.saveOrUpdate(we);
        } catch (Exception e) {
            System.err.println("Exception while saving tried word: " + e.getMessage());
        }
    }

    public Optional<WordEntity> getWordByText(String word) {
        List<WordEntity> we = List.of();
        try {
            we = databaseConnector
                    .getByQuery(WordEntity.class,
                            "from WordEntity where text='" +
                                    word + "'"
                    );
        } catch (Exception e) {
            System.err.println("Error while finding word in da ta base: " + e);
        }
        return we.isEmpty()
                ? Optional.empty()
                : Optional.of(we.get(0));
    }

    //TODO: get or create word attempt including get or create user
    public Optional<WordAttempt> getOrCreateWordAttempt(User chatUser, int attemptsCount) {
        var wordOptional = getCurrentWord();
        if (wordOptional.isEmpty()) {
            return Optional.empty();
        }

        var userId = databaseConnector.getOrCreateUser(chatUser).getId();
        var wordId = wordOptional.get().getId();
        WordAttempt wordAttempt;

        List<WordAttempt> wordAttemptsList = List.of();
        try {
            wordAttemptsList = databaseConnector
                    .getByQuery(WordAttempt.class,
                            "from WordAttempt where word_id='" +
                                    wordId + "' and user_id='" +
                                    userId + "'"
                    );
        } catch (Exception e) {
            System.err.println("Error while finding word_attempt in da ta base: " + e);
        }

        if (wordAttemptsList.isEmpty()) {
            wordAttempt = new WordAttempt(wordId, userId, attemptsCount);
            databaseConnector.saveOrUpdate(wordAttempt);
        } else {
            wordAttempt = wordAttemptsList.get(0);
        }

        return Optional.of(wordAttempt);
    }

    public boolean isUserFirstWhoTriedToGuess(User chatUser, Long wordId) {
        List<WordAttempt> wordAttemptsList = List.of();
        Long userId = chatUser == null ? 0 :
                chatUser.getId();

        try {
            wordAttemptsList = databaseConnector
                    .getByQuery(WordAttempt.class,
                            "from WordAttempt where word_id='" + wordId + "'"
                    );
        } catch (Exception e) {
            System.err.println("Error while finding word_attempt in da ta base: " + e);
        }

        if (wordAttemptsList.isEmpty()) {
            return true;
        }

        return
                wordAttemptsList.stream()
                        .mapToLong(WordAttempt::getId)
                        .min()

                        ==

                        wordAttemptsList.stream()
                                .filter(wa -> wa.getUserId().equals(userId))
                                .mapToLong(WordAttempt::getId)
                                .findFirst();
    }

    public boolean isAnyWordAttempts(Long wordId) {
        List<WordAttempt> wordAttemptsList = List.of();

        try {
            wordAttemptsList = databaseConnector
                    .getByQuery(WordAttempt.class,
                            "from WordAttempt where word_id='" + wordId + "'"
                    );
        } catch (Exception e) {
            System.err.println("Error while finding word_attempt in da ta base: " + e);
        }

        return !wordAttemptsList.isEmpty();
    }

    public void saveWordAttempt(WordAttempt wordAttempt) {
        databaseConnector.saveOrUpdate(wordAttempt);
    }

    public Optional<String> getCurrentWordForChat(Long chatId) {
        Optional<String> currentWordOpt = Optional.empty();

        var wordsHistory = databaseConnector.getByQuery(WordsHistory.class,
                "from WordsHistory where chatId=" + chatId
                + " and isGuessed=false");

        if (wordsHistory.size() == 1) {
            var currentWordGame = wordsHistory.get(0);
            var currentWordId = currentWordGame.getWordId();
            var currentWord= databaseConnector.getById(WordEntity2.class, currentWordId);
            if (currentWord.isPresent()) {
                currentWordOpt = Optional.of(currentWord.get().getText());
            }
        }

        return currentWordOpt;
    }

    //TODO: ?????????????? ?????????????? last_tried_words(word varchar, chat_id integer)
    public Optional<String> getLastTriedWordForChat(Long chatId) {
        return null;
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
