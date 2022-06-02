package ru.ikusov.training.telegrambot.services.wordle;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.User;
import ru.ikusov.training.telegrambot.services.UserNameGetter;
import ru.ikusov.training.telegrambot.utils.MyMath;
import ru.ikusov.training.telegrambot.utils.MyString;

import static ru.ikusov.training.telegrambot.services.wordle.WordleUtils.*;

@Component
public class WordleService3 implements WordleService {
    private static final int MAX_ALLOWED_ATTEMPTS = 2;
        private static final long WORDS_INTERVAL = 24 * 3600;
//    private static final long WORDS_INTERVAL = 6;

    private static final String BEE = "\uD83D\uDC1D";

    private String currentWord;
    private String lastGuessWord;
    private GameStatus gameStatus;

    private WordleRepository wordleRepository;

    @Autowired
    public WordleService3(WordleRepository wordleRepository) {
        this.wordleRepository = wordleRepository;
        this.gameStatus = GameStatus.NOT_STARTED;
    }

    public String startGame(Long chatId) {
        //TODO: wordleRepository.getCurrentWord2
        //TODO: отдельная таблица words_history: word_id, chat_id, is_guessed, guesser_id
        //create table words_history(word_id integer, chat_id integer, is_guessed bool, guesser_id integer);
        var currentWE = wordleRepository.getCurrentWordForChat(chatId);
        System.out.println("start game: current word from DB: " + currentWE.isPresent());

        //слово уже загадано
        if (currentWE.isPresent()) {
            currentWord = currentWE.get();

            //TODO: подумать, надо ли вообще это Last Tried Word; так как слова для проверки будут браться с gramota.ru
            var lastGuessWordWE = wordleRepository.getLastTriedWordForChat(chatId);
            String s;
            if (lastGuessWordWE.isEmpty()) {
                s = "";
            } else {
                s = lastGuessWordWE.get();
            }

            lastGuessWord = formatToMarkdownV2(s, compareWords(s, currentWord));
            return MyString.markdownv2Format("Слово уже загадано! Предыдущая попытка отгадки: ") + lastGuessWord;
        }

        //ЭТУ ПРОВЕРКУ УБИРАЕМ, НЕ БУДЕТ ОГРАНИЧЕНИЯ
        //слово не загадано, прошло меньше интервала времени между предыдущей загадкой
//        currentWE = wordleRepository.getLastGuessedWord();
//        if (currentWE.isPresent()) {
//            var interval = System.currentTimeMillis() / 1000 - currentWE.get().getTimeStamp();
//            if (interval < WORDS_INTERVAL) {
//                var pendingTime = WORDS_INTERVAL - interval;
//                return MyString.markdownv2Format("Следующее слово будет доступно через " + MyMath.secondsToReadableTimeVin(pendingTime) + "!");
//            }
//        }

        //TODO: wordleRepo.getNextRandomWordForChatId(Long chatId)
        //слово не загадано, отгаданных слов нет либо прошло больше интервала времени между предыдущей загадкой
        currentWord = wordleRepository.getRandomWord();

        //TODO: количество букв в слове может теперь быть не пять
        String пяти = "пяти";
        return MyString.markdownv2Format("Загадал русское слово из " + пяти + " букв!");
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
