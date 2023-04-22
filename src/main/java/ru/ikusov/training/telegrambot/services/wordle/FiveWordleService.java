package ru.ikusov.training.telegrambot.services.wordle;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.User;
import ru.ikusov.training.telegrambot.dao.wordle.OldWordleRepository;
import ru.ikusov.training.telegrambot.services.UserNameGetter;
import ru.ikusov.training.telegrambot.utils.MyMath;
import ru.ikusov.training.telegrambot.utils.MyString;

import static ru.ikusov.training.telegrambot.services.wordle.WordleUtils.*;

@Component
//@Primary
public class FiveWordleService implements WordleService {
    private static final int MAX_ALLOWED_ATTEMPTS = 2;
        private static final long WORDS_INTERVAL = 3600;
//    private static final long WORDS_INTERVAL = 6;

    private static final String BEE = "\uD83D\uDC1D";

    private String currentWord;
    private String lastGuessWord;
    private GameStatus gameStatus;

    private OldWordleRepository wordleRepository;

    @Autowired
    public FiveWordleService(OldWordleRepository wordleRepository) {
        this.wordleRepository = wordleRepository;
        this.gameStatus = GameStatus.NOT_STARTED;
    }

    public String startGame(Long chatId, Long userId, int wordLen) {
        var currentWE = wordleRepository.getCurrentWord();
        System.out.println("start game: current word from DB: " + currentWE.isPresent());

        //слово уже загадано
        if (currentWE.isPresent()) {
            currentWord = currentWE.get().getText();
            var lastGuessWordWE = wordleRepository.getLastTriedWord();
            String s;
            if (lastGuessWordWE.isEmpty()) {
                s = "";
            } else {
                s = lastGuessWordWE.get().getText();
            }

            lastGuessWord = formatToMarkdownV2(s, compareWords(s, currentWord));
            return MyString.markdownv2Format("Слово уже загадано! Предыдущая попытка отгадки: ") + lastGuessWord;
        }

        //слово не загадано, прошло меньше интервала времени между предыдущей загадкой
        currentWE = wordleRepository.getLastGuessedWord();
        if (currentWE.isPresent()) {
            var interval = System.currentTimeMillis() / 1000 - currentWE.get().getTimeStamp();
            if (interval < WORDS_INTERVAL) {
                var pendingTime = WORDS_INTERVAL - interval;
                return MyString.markdownv2Format("Следующее слово будет доступно через " + MyMath.secondsToReadableTimeVin(pendingTime) + "!");
            }
        }

        //слово не загадано, отгаданных слов нет либо прошло больше интервала времени между предыдущей загадкой
        currentWord = wordleRepository.getRandomWord();
        return MyString.markdownv2Format("Загадал русское слово из пяти букв!");
    }

    public String checkWord(String word, User chatUser, Long chatId) {
        var optionalCurrentWord = wordleRepository.getCurrentWord();

        if (optionalCurrentWord.isEmpty()) {
            return "";
        }

        var currentWordEntity = optionalCurrentWord.get();
        var triedWord = wordleRepository.getWordByText(word);

        //слова нету в базе данных, орём, что нету мол
        if (triedWord.isEmpty()) {
            return MyString.markdownv2Format("В моём словаре нет слова \"" + word + "\", попробуйте другое!");
        }

        //слово таки есть в базе данных, проверяем, не превысил ли пользователь количество попытков
        //для первого угадывающего фора в +1 попытку
        int currentAllowedAttempts = !wordleRepository.isAnyWordAttempts(currentWordEntity.getId())
                ? MAX_ALLOWED_ATTEMPTS + 1
                : MAX_ALLOWED_ATTEMPTS;

        var optionalWordAttempt = wordleRepository.getOrCreateWordAttempt(chatUser, currentAllowedAttempts);
        if (optionalWordAttempt.isEmpty()) {
            return "";
        }

        var wordAttempt = optionalWordAttempt.get();
        var wordAttemptsCount = wordAttempt.getRemainingAttemptsCount();

        if (wordAttemptsCount <= 0) {
            return MyString.markdownv2Format(
                    "Достигнуто максимальное количество попыток для пользователя "
                            + UserNameGetter.getUserName(chatUser) +
                            "!"
            );
        }

        //слово таки есть в базе данных, сравняем с правильным
        currentWord = currentWordEntity.getText();
        var guessResult = compareWords(word, currentWord);
        var formattedWord = formatToMarkdownV2(word, guessResult);

        //если не совпадает с правильным
        if (!isFullOfTwos(guessResult)) {
            wordAttempt.setRemainingAttemptsCount(--wordAttemptsCount);
            wordleRepository.saveWordAttempt(wordAttempt);
            wordleRepository.setLastTriedWord(triedWord.get());
            return formattedWord +
                    MyString.markdownv2Format(
                            "\nДля пользователя "
                                    + UserNameGetter.getUserName(chatUser)
                                    + " осталось попыток: "
                                    + (wordAttemptsCount)
                    );
        }

        //если всё правильно
        wordAttempt.setGuessed(true);
        wordAttempt.setRemainingAttemptsCount(wordAttemptsCount + 1);
        wordleRepository.saveWordAttempt(wordAttempt);
        wordleRepository.setLastGuessedWord(triedWord.get());
        if (currentWord.equals("пчела")) {
            formattedWord = formattedWord + " " + BEE;
        }
        return MyString.markdownv2Format("Совершенно верно! Правильный ответ - ") + formattedWord;
    }

    @Override
    public boolean isWordleAnswer(String text, Long chatId) {
        var len = 5;
        if (len != text.length()) {
            return false;
        }
        return WordleUtils.isWordleAnswer(text);
    }

    public String checkWord(String word) {
        var weOpt = wordleRepository.getCurrentWord();

        if (weOpt.isEmpty()) {
            return "";
        }

        var we = weOpt.get();
        var triedWord = wordleRepository.getWordByText(word);

        //слова нету в базе данных, орём, что нету мол
        if (triedWord.isEmpty()) {
            return MyString.markdownv2Format("В моём словаре нет слова \"" + word + "\", попробуйте другое!");
        }

        //слово таки есть в базе данных, сравняем с правильным
        currentWord = we.getText();
        var guessResult = compareWords(word, currentWord);
        var formattedWord = formatToMarkdownV2(word, guessResult);

        //если не совпадает с правильным
        if (!isFullOfTwos(guessResult)) {
            wordleRepository.setLastTriedWord(triedWord.get());
            return formattedWord;
        }

        //если всё правильно
        wordleRepository.setLastGuessedWord(triedWord.get());
        return MyString.markdownv2Format("Совершенно верно! Правильный ответ - ") + formattedWord;
    }

    private boolean wordIsInDB(String word) {
        return wordleRepository.isWordInDB(word);
    }

    public enum GameStatus {
        NOT_STARTED,
        STARTED,
        LOCKED
    }
}
