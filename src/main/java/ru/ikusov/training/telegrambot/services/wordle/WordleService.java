package ru.ikusov.training.telegrambot.services.wordle;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import ru.ikusov.training.telegrambot.utils.MyMath;
import ru.ikusov.training.telegrambot.utils.MyString;

import static ru.ikusov.training.telegrambot.services.wordle.WordleUtils.*;

@Component
public class WordleService {
    private static final long WORDS_INTERVAL = 24 * 3600;
//    private static final long WORDS_INTERVAL = 6;
    private String currentWord;
    private String lastGuessWord;
    private GameStatus gameStatus;

    private WordleRepository wordleRepository;

    @Autowired
    public WordleService(WordleRepository wordleRepository) {
        this.wordleRepository = wordleRepository;
        this.gameStatus = GameStatus.NOT_STARTED;
    }

    public String startGame() {
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
                return MyString.markdownv2Format("Следующее слово будет доступно через " + MyMath.secondsToReadableTime(pendingTime) + "!");
            }
        }

        //слово не загадано, отгаданных слов нет либо прошло больше интервала времени между предыдущей загадкой
        currentWord = wordleRepository.getRandomWord();
        return MyString.markdownv2Format("Загадал русское слово из пяти букв!");
    }

    public String checkWord(String word) {
        var weOpt= wordleRepository.getCurrentWord();

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
