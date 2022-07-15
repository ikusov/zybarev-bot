package ru.ikusov.training.telegrambot.services.wordle;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.User;
import ru.ikusov.training.telegrambot.services.UserNameGetter;
import ru.ikusov.training.telegrambot.utils.Linguistic;
import ru.ikusov.training.telegrambot.utils.MyMath;
import ru.ikusov.training.telegrambot.utils.MyString;

import java.io.IOException;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

import static ru.ikusov.training.telegrambot.services.wordle.WordleUtils.*;

@Component
@Primary
public class WordleService3 implements WordleService {
    private final Function<String, Integer> allowedAttemptsGetter =
            w ->
//                    w.length()/2;
                    2;

    private static final String BEE = "\uD83D\uDC1D";

    private final WordleRepository2 wordleRepository;
    private final List<WordChecker> wordCheckers;

    @Autowired
    public WordleService3(WordleRepository2 wordleRepository, List<WordChecker> wordCheckers) {
        this.wordleRepository = wordleRepository;
        this.wordCheckers = wordCheckers;
    }

    public String startGame(Long chatId, int wordLen) {
        var currentWord = wordleRepository.getCurrentWordForChat(chatId);

        //слово уже загадано
        if (!currentWord.isEmpty()) {
            String wordLength = Linguistic.russianNumberNamesGenetive.getOrDefault(
                    currentWord.length(),
                    String.valueOf(currentWord.length()));

            String message = "Слово из " +
                    wordLength +
                    " букв уже загадано! Пока никто не пробовал отгадать.";

            var lastTriedWords = wordleRepository.getTriedWordsForChat(chatId);
            if (lastTriedWords.isEmpty()) {
                return MyString.markdownv2Format(message);
            }

            String finalCurrentWord = currentWord;
            var markDownV2FormattedWordList = lastTriedWords.stream()
                    .map(w -> formatToMarkdownV2(w, compareWords(w, finalCurrentWord)))
                    .collect(Collectors.joining("\n"));
            return MyString.markdownv2Format("Слово уже загадано! Предыдущие попытки отгадок:\n")
                    + markDownV2FormattedWordList;
        }

        //слово не загадано
//        currentWord = wordleRepository.getNextRandomWordForChat(chatId);
        currentWord = wordleRepository.getNextRandomWordForChat(chatId, wordLen);

        String wordLength = Linguistic.russianNumberNamesGenetive.getOrDefault(
                currentWord.length(),
                String.valueOf(currentWord.length()));
        return MyString.markdownv2Format("Загадал русское слово из " + wordLength + " букв!");
    }

    public String checkWord(String word, User chatUser, Long chatId) {
        var currentWord = wordleRepository.getCurrentWordForChat(chatId);

        if (currentWord.isEmpty()) {
            return "";
        }

        //проверяем вордчекерами, существует ли слово
        boolean wordExists = false;
        for (var wordChecker : wordCheckers) {
            try {
                wordExists = wordExists || wordChecker.check(word);
            } catch (IOException ignored) {
            }
        }

        //слова не существует, орём, что нету мол
        if (!wordExists) {
            return MyString.markdownv2Format("В моём словаре нет слова \"" + word + "\", попробуйте другое!");
        }

        //проверяем, не превысил ли пользователь количество попытков
        //для первого угадывающего фора в +1 попытку
        var attemptsCount = wordleRepository
                .getOrCreateWordAttempt(chatUser.getId(), chatId, wordleRepository
                        .isAnyWordAttempts(chatId)
                        ? allowedAttemptsGetter.apply(word) :
                        allowedAttemptsGetter.apply(word) + 1);

        if (attemptsCount <= 0) {
            var attemptTTLSeconds = wordleRepository.getWordAttemptTTL(chatUser.getId(), chatId);
            return MyString.markdownv2Format(
                    "Достигнуто максимальное количество попыток для пользователя "
                            + UserNameGetter.getUserName(chatUser) +
                            "! Следующая попытка угадать данное слово будет доступна через "
                            + MyMath.secondsToReadableTimeVin(attemptTTLSeconds)
                            + "!"
            );
        }

        //сохраниям попытку угадывания слова
        wordleRepository.saveWordAttempt(word, chatUser.getId(), chatId);

        //слово таки есть в базе данных, сравняем с правильным
        var guessResult = compareWords(word, currentWord);
        var formattedWord = formatToMarkdownV2(word, guessResult);
        String response;

        //если не совпадает с правильным
        if (!isFullOfTwos(guessResult)) {
            attemptsCount--;
            response = formattedWord +
                    MyString.markdownv2Format(
                            "\nДля пользователя "
                                    + UserNameGetter.getUserName(chatUser)
                                    + " осталось попыток: "
                                    + attemptsCount
                    );
            //если всё правильно
        } else {
            if (currentWord.equals("пчела")) {
                formattedWord = formattedWord + " " + BEE;
            }
            wordleRepository.setRightAnswer(chatId);
            response = MyString
                    .markdownv2Format("Совершенно верно! Правильный ответ - ") + formattedWord;
        }

        return response;
    }

    @Override
    public boolean isWordleAnswer(String text, Long chatId) {
        if (!WordleUtils.isWordleAnswer(text)) {
            return false;
        }

        String curWord = wordleRepository.getCurrentWordForChat(chatId);
        var len = curWord.length();

        return len == text.length();
    }
}
