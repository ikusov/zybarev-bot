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

    private WordleRepository2 wordleRepository;

    @Autowired
    public WordleService3(WordleRepository2 wordleRepository) {
        this.wordleRepository = wordleRepository;
        this.gameStatus = GameStatus.NOT_STARTED;
    }

    public String startGame(Long chatId) {
        //TODO: wordleRepository.getCurrentWord2
        //TODO: отдельная таблица words_history: word_id, chat_id, is_guessed, guesser_id
        //create table words_history(word_id integer, chat_id integer, is_guessed bool, guesser_id integer);
        var currentWord = wordleRepository.getCurrentWordForChat(chatId);

        //слово уже загадано
        if (currentWord != null) {
            //TODO: подумать, надо ли вообще это Last Tried Word; так как слова для проверки будут браться с gramota.ru
            var lastTriedWord = wordleRepository.getLastTriedWordForChat(chatId);
            String s;
            if (lastTriedWord == null) {
                s = "";
            } else {
                s = lastTriedWord;
            }

            lastGuessWord = formatToMarkdownV2(s, compareWords(s, this.currentWord));
            return MyString.markdownv2Format("Слово уже загадано! Предыдущая попытка отгадки: ") + lastGuessWord;
        }

        //TODO: wordleRepo.getNextRandomWordForChatId(Long chatId)
        //слово не загадано, отгаданных слов нет либо прошло больше интервала времени между предыдущей загадкой
        this.currentWord = wordleRepository.getRandomWord();

        //TODO: количество букв в слове может теперь быть не пять
        String пяти = "пяти";
        return MyString.markdownv2Format("Загадал русское слово из " + пяти + " букв!");
    }

    public String checkWord(String word, User chatUser, Long chatId) {
        var optionalCurrentWord = wordleRepository.getCurrentWordForChat(chatId);

        if (optionalCurrentWord.isEmpty()) {
            return "";
        }

        var currentWordEntity = optionalCurrentWord;
        var triedWord = wordleRepository.getWordByText(word);

        //слова нету в базе данных, орём, что нету мол
        if (triedWord.isEmpty()) {
            return MyString.markdownv2Format("В моём словаре нет слова \"" + word + "\", попробуйте другое!");
        }

        //слово таки есть в базе данных, проверяем, не превысил ли пользователь количество попытков
        //для первого угадывающего фора в +1 попытку
        int currentAllowedAttempts = !wordleRepository.isAnyWordAttempts(currentWordEntity, chatId)
                ? MAX_ALLOWED_ATTEMPTS + 1
                : MAX_ALLOWED_ATTEMPTS;

        var optionalWordAttempt = wordleRepository.getOrCreateWordAttempt(chatUser, chatId, currentAllowedAttempts);
        if (optionalWordAttempt == -1) {
            return "";
        }

        var wordAttempt = optionalWordAttempt;
        var wordAttemptsCount = wordAttempt;

        if (wordAttemptsCount <= 0) {
            return MyString.markdownv2Format(
                    "Достигнуто максимальное количество попыток для пользователя "
                            + UserNameGetter.getUserName(chatUser) +
                            "!"
            );
        }

        //слово таки есть в базе данных, сравняем с правильным
        currentWord = currentWordEntity;
        var guessResult = compareWords(word, currentWord);
        var formattedWord = formatToMarkdownV2(word, guessResult);

        //если не совпадает с правильным
        if (!isFullOfTwos(guessResult)) {
            wordAttempt--;
            wordleRepository.saveWordAttempt(chatUser, chatId, wordAttempt);
            wordleRepository.setLastTriedWord(triedWord);
            return formattedWord +
                    MyString.markdownv2Format(
                            "\nДля пользователя "
                                    + UserNameGetter.getUserName(chatUser)
                                    + " осталось попыток: "
                                    + (wordAttemptsCount)
                    );
        }

        //если всё правильно
        wordleRepository.saveWordAttempt(chatUser, chatId, wordAttempt);
        wordleRepository.setLastGuessedWord(triedWord, chatId);
        if (currentWord.equals("пчела")) {
            formattedWord = formattedWord + " " + BEE;
        }
        return MyString.markdownv2Format("Совершенно верно! Правильный ответ - ") + formattedWord;
    }

    public enum GameStatus {
        NOT_STARTED,
        STARTED,
        LOCKED
    }
}
