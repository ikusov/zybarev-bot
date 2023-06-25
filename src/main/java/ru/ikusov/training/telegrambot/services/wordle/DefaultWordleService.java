package ru.ikusov.training.telegrambot.services.wordle;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.User;
import ru.ikusov.training.telegrambot.dao.wordle.WordleRepository;
import ru.ikusov.training.telegrambot.model.WordleEventDto;
import ru.ikusov.training.telegrambot.model.wordle.WordleUserPointsDto;
import ru.ikusov.training.telegrambot.services.UserNameGetter;
import ru.ikusov.training.telegrambot.utils.Linguistic;
import ru.ikusov.training.telegrambot.utils.MyMath;

import java.io.IOException;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

import static ru.ikusov.training.telegrambot.services.wordle.WordleMessageProvider.formattedUserPointsMessage;
import static ru.ikusov.training.telegrambot.services.wordle.WordleUtils.*;
import static ru.ikusov.training.telegrambot.utils.MyString.markdownv2Format;

@Component
@Primary
public class DefaultWordleService implements WordleService {
    private static final Logger log = LoggerFactory.getLogger(DefaultWordleService.class);
    private static final String BEE = "\uD83D\uDC1D";
    private final Function<String, Integer> allowedAttemptsGetter = w -> 2;

    private final WordleRepository wordleRepository;
    private final List<WordChecker> wordCheckers;
    private final WordlePointsService wordlePointsService;

    @Autowired
    public DefaultWordleService(
            WordleRepository wordleRepository,
            List<WordChecker> wordCheckers,
            WordlePointsService wordlePointsService
    ) {
        this.wordleRepository = wordleRepository;
        this.wordCheckers = wordCheckers;
        this.wordlePointsService = wordlePointsService;
    }

    public String startGame(Long chatId, Long userId, int wordLen) {
        log.debug("Trying to get current word for chatId '{}' from repository...", chatId);
        var currentWord = wordleRepository.getCurrentWordForChat(chatId);
        log.debug("Getted current word from repository!");

        //слово уже загадано
        if (!currentWord.isEmpty()) {
            log.debug("Current word is not empty! Trying to construct pretty letter count...");
            String wordLength = Linguistic.russianNumberNamesGenetive.getOrDefault(
                    currentWord.length(),
                    String.valueOf(currentWord.length()));
            log.debug("Resulted pretty count: '{}'", wordLength);

            String message = "Слово из " +
                    wordLength +
                    " букв уже загадано! Пока никто не пробовал отгадать.";

            log.debug("Trying to get attempts from repository...");
            var lastTriedWords = wordleRepository.getTriedWordsForChat(chatId);
            if (lastTriedWords.isEmpty()) {
                log.debug("No attempts found! Returning message '{}'", message);
                return markdownv2Format(message);
            }

            log.debug("Attempts found. Constructing attempts list to show...");
            String finalCurrentWord = currentWord;
            var markDownV2FormattedWordList = lastTriedWords.stream()
                    .map(w -> formatToMarkdownV2(w, compareWords(w, finalCurrentWord)))
                    .collect(Collectors.joining("\n"));
            log.debug("Returning constructed markdownV2 list: '{}'", markDownV2FormattedWordList);

            return markdownv2Format("Слово уже загадано! Предыдущие попытки отгадок:\n")
                    + markDownV2FormattedWordList;
        }

        //слово не загадано
//        currentWord = wordleRepository.getNextRandomWordForChat(chatId);
        log.debug("Current word is empty! Getting next word for chat from repository...");
        currentWord = wordleRepository.getNextRandomWordForChat(chatId, wordLen);
        log.debug("The word was successfully getted! In has {} letters!", currentWord.length());

        String wordLength = Linguistic.russianNumberNamesGenetive.getOrDefault(
                currentWord.length(),
                String.valueOf(currentWord.length()));
        log.debug("Constructed and returned pretty word letter count: '{}'", wordLength);

        //now pls save event for new wordle for chat
        WordleEventDto we = new WordleEventDto(chatId, userId, currentWord, null, false);
        wordleRepository.saveWordleEvent(we);

        return markdownv2Format("Загадал русское слово из " + wordLength + " букв!");
    }

    public String checkWord(String userWord, User chatUser, Long chatId) {
        var currentWord = wordleRepository.getCurrentWordForChat(chatId);
        var word = WordleUtils.toWordleString(userWord);
        Long userId = chatUser.getId();

        if (currentWord.isEmpty()) {
            return "";
        }

        //проверяем вордчекерами, существует ли слово
        boolean wordExists = false;
        for (var wordChecker : wordCheckers) {
            try {
                wordExists = wordExists || wordChecker.check(word);
                wordExists = wordExists || wordChecker.check(userWord);
            } catch (IOException ignored) {
            }
        }

        //слова не существует, орём, что нету мол
        if (!wordExists) {
            return markdownv2Format("В моём словаре нет слова \"" + userWord + "\", попробуйте другое!");
        }

        //слово таки есть в базе данных, сравняем с правильным
        int[] guessResult = compareWords(word, currentWord);
        String formattedWord = formatToMarkdownV2(word, guessResult);
        boolean isAddBonusAttempt = isAlmostFullOfTwos(guessResult);

        String additionalMessage = "";

        //проверяем, не превысил ли пользователь количество попытков
        //для первого угадывающего фора в +1 попытку
        var attemptsCount = wordleRepository.getWordAttemptsCount(
                userId,
                chatId,
                !wordleRepository.isAnyWordAttempts(chatId)
                        ? allowedAttemptsGetter.apply(word) + 1
                        : allowedAttemptsGetter.apply(word)
        );

        if (attemptsCount == 1 && isAddBonusAttempt) {
            attemptsCount = 2;
            additionalMessage = markdownv2Format("\nДобавлена бонус-попытка!\n")
                    + "*" + markdownv2Format("FINISH IT!") + "*";
        }

        String userName = UserNameGetter.getUserName(chatUser);
        if (attemptsCount <= 0) {
            var attemptTTLSeconds = wordleRepository.getWordAttemptTTL(userId, chatId);
            return markdownv2Format(
                    "Достигнуто максимальное количество попыток для пользователя "
                            + userName +
                            "! Следующая попытка угадать данное слово будет доступна через "
                            + MyMath.secondsToReadableTimeVin(attemptTTLSeconds)
                            + "!"
            );
        }

        //сохраниям попытку угадывания слова
        wordleRepository.saveWordAttempt(word, chatId);
        if (!isAddBonusAttempt) {
            wordleRepository.decreaseAttemptsCount(userId, chatId);
        }

        String response;

        //если не совпадает с правильным
        if (!isFullOfTwos(guessResult)) {
            response = formattedWord
                    + markdownv2Format("\nДля пользователя "
                        + userName
                        + " осталось попыток: "
                        + --attemptsCount)
                    + additionalMessage;
            //если всё правильно
        } else {
            if (currentWord.equals("пчела")) {
                formattedWord = formattedWord + " " + BEE;
            }
            wordleRepository.setRightAnswer(chatId);
            response = markdownv2Format("Совершенно верно! Правильный ответ - ") + formattedWord;

            //no counting and saving points for private chats (where chatId == userId)
            if (!chatId.equals(userId)) {
                try {
                    WordleUserPointsDto wordleUserPoints = wordlePointsService.savePoints(chatId, userId);
                    response += "\n" + formattedUserPointsMessage(
                            userName,
                            wordleUserPoints.getCurrentPoints(),
                            wordleUserPoints.getSumPoints()
                    );
                } catch (Exception e) {
                    log.error("Error while saving points to database!");
                }
            }
        }

        WordleEventDto we = new WordleEventDto(chatId, userId, currentWord, word, isFullOfTwos(guessResult));
        wordleRepository.saveWordleEvent(we);

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
