package ru.ikusov.training.telegrambot.services.wordle;

import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Chat;
import org.telegram.telegrambots.meta.api.objects.User;
import ru.ikusov.training.telegrambot.dao.wordle.WordleRepository;
import ru.ikusov.training.telegrambot.dao.wordle.dto.WordleWordDto;
import ru.ikusov.training.telegrambot.model.WordleEventDto;
import ru.ikusov.training.telegrambot.model.wordle.WordleUserPointsDto;
import ru.ikusov.training.telegrambot.services.UserNameGetter;
import ru.ikusov.training.telegrambot.utils.Linguistic;
import ru.ikusov.training.telegrambot.utils.MyMath;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

import static ru.ikusov.training.telegrambot.services.wordle.WordleMessageProvider.formattedUserPointsMessage;
import static ru.ikusov.training.telegrambot.services.wordle.WordleUtils.*;
import static ru.ikusov.training.telegrambot.utils.MyString.markdownv2Format;

@Component
@Primary
@RequiredArgsConstructor
public class DefaultWordleService implements WordleService {
    private static final Logger log = LoggerFactory.getLogger(DefaultWordleService.class);
    private static final String BEE = "\uD83D\uDC1D";

    @Value("#{environment.wordle_max_attempts}")
    private final Integer allowedAttemptsCount = 2;
    private final Function<String, Integer> allowedAttemptsGetter = w -> allowedAttemptsCount;

    private final WordleRepository wordleRepository;
    private final List<WordChecker> wordCheckers;
    private final WordlePointsService wordlePointsService;
    private final WordleStatService wordleStatService;

    @Override
    public String startGame(Chat chat, Long userId, int wordLen) {
        Long chatId = chat.getId();
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
        log.debug("Current word is empty! Getting next word for chat from repository...");
        WordleWordDto wordForChatDto = wordleRepository.getNextRandomWordForChat(chatId, wordLen);
        currentWord = wordForChatDto.word();
        log.debug("The word was successfully getted! In has {} letters!", currentWord.length());

        String wordLength = Linguistic.russianNumberNamesGenetive.getOrDefault(
                currentWord.length(),
                String.valueOf(currentWord.length()));
        log.debug("Constructed and returned pretty word letter count: '{}'", wordLength);

        //now pls save event for new wordle for chat
        WordleEventDto we = new WordleEventDto(chatId, userId, currentWord, null, false);
        wordleRepository.saveWordleEvent(we);

        String chatName = StringUtils.isEmpty(chat.getTitle()) ? "" : chat.getTitle();
        long madeWordsCount = wordleStatService.getMadeWordsCountForChat(chat);
        long lastWordCount = wordForChatDto.wordsCount() + madeWordsCount;
        return markdownv2Format("Загадал русское слово из " + wordLength + " букв!")
                + "\n"
                + markdownv2Format(
                        "(Слово № "
                                + madeWordsCount
                                +
                                " из "
                                + lastWordCount
                                + " для чата "
                                + chatName
                                + ")");
    }

    public String checkWord(String userWord, User chatUser, Long chatId) {
        var currentWord = wordleRepository.getCurrentWordForChat(chatId);
        var word = WordleUtils.toWordleString(userWord);
        Long userId = chatUser.getId();

        if (currentWord.isEmpty()) {
            return "";
        }

        //<editor-fold desc="будем проверять вордчекерами, существует ли слово">
        //проверка не нужна, если юзер угадал слово (т.к. в БД не все слова чекаются)
        boolean wordExists = currentWord.equals(word) || currentWord.equals(userWord);
        List<String> checkedByList = new ArrayList<>(wordCheckers.size());

        for (var wordChecker : wordCheckers) {
            try {
                if (wordExists) {
                    break;
                }
                wordExists = wordChecker.check(word);
                checkedByList.add(wordChecker.getName());

                wordExists = wordExists || wordChecker.check(userWord);
            } catch (IOException ignored) {
            }
        }

        //слова не существует, орём, что нету мол
        if (!wordExists) {
            return markdownv2Format("В моих словарях (доступно "
                    + checkedByList.size()
                    + " из "
                    + wordCheckers.size()
                    + ") не найдено существительного в именительном падеже \""
                    + userWord +
                    "\", попробуйте другое!");
        }
        //</editor-fold>

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
