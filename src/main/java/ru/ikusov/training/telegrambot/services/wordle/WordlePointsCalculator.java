package ru.ikusov.training.telegrambot.services.wordle;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cglib.core.internal.Function;
import ru.ikusov.training.telegrambot.services.wordle.WordleAttemptsLettersCounter.LetterCounters;

import java.util.List;

import static ru.ikusov.training.telegrambot.services.wordle.WordleAttemptsLettersCounter.countLetters;
import static ru.ikusov.training.telegrambot.utils.Linguistic.RUSSIAN_ALPHABET_LETTERS_COUNT;

/**
 * Класс подсчёта очков
 * <p>
 * Принципы подсчёта очков за слово:
 * 1. Суммарное количество очков за все итерации угадывания слова: 12 + wordLen == от 16 до 20 очков
 * 2. Первая итерация - чистая догадка. Кажется, что здесь нужно выдавать просто лаки-бонусы.
 * 3. Пусть максимальное количество лаки-бонусов будет 5. Тогда лаки-бонусы считаем по формуле:
 * N = guessedLettersCount * 5 / wordLen
 * 4. Дальше надо считать по количеству новых угаданных и переставленных букв, вроде кажется, что по той же формуле
 */

@Slf4j
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class WordlePointsCalculator {
    private static final Function<Integer, Float> maxPointsForLetterComputer =
            wordLength -> (wordLength + 12f) / wordLength;
    private static final float GUESSED_LETTER_COEF = 1f;
    private static final float PLACED_LETTER_COEF = .5f;
    private static final float KNOWN_LETTER_COEF = .5f;

    public static int countPointsForUnGuessed(String rightWord, String currentWord, List<String> triedWords) {
        LetterCounters lettersCounter = countLetters(rightWord, currentWord, triedWords);
        return triedWords.size() == 0
                ? countLuckyPoints(rightWord.length(), lettersCounter)
                : countPoints(rightWord.length(), lettersCounter);
    }

    public static int countPointsForGuessed(String rightWord, String currentWord, List<String> triedWords) {
        int wordLen = rightWord.length();
        LetterCounters lettersCounterForBonusik = countLetters(rightWord, rightWord, List.of());
        LetterCounters lettersCounter = countLetters(rightWord, currentWord, triedWords);

        //посчитать бонусик
        //разница между количеством очков, которое игрок получил бы за угадывание слова с первой попытки
        //по "полной" схеме
        //и количеством очков по "лаки" схеме
        int bonusik = countPoints(wordLen, lettersCounterForBonusik)
                - countLuckyPoints(wordLen, lettersCounterForBonusik);

        return triedWords.size() == 0
                ? countLuckyPoints(wordLen, lettersCounter)
                : countPoints(wordLen, lettersCounter) + bonusik;
    }

    static int countPoints(int wordLen, LetterCounters lettersCounter) {
        int BIG_INTEGER = 1000; // чтобы маленькие значения не занулялись при целочисленных вычислениях
        Float MAX_LETTER_POINTS = maxPointsForLetterComputer.apply(wordLen);

        int guessedCoef = (int) (BIG_INTEGER * GUESSED_LETTER_COEF * MAX_LETTER_POINTS
                * lettersCounter.leftLettersCount() / RUSSIAN_ALPHABET_LETTERS_COUNT);
        int placedCoef = (int) (BIG_INTEGER * PLACED_LETTER_COEF * MAX_LETTER_POINTS);
        int knownCoef = (int) (BIG_INTEGER * KNOWN_LETTER_COEF * MAX_LETTER_POINTS
                * lettersCounter.leftLettersCount() / RUSSIAN_ALPHABET_LETTERS_COUNT);

        int multiPoints = guessedCoef * lettersCounter.guessedLettersCount()
                + placedCoef * lettersCounter.placedLettersCount()
                + knownCoef * lettersCounter.knownLettersCount();

        return multiPoints == 0
                ? 0
                : multiPoints < BIG_INTEGER
                ? 1
                : multiPoints / BIG_INTEGER;
    }

    //TODO: подумать с точки зрения логики игры, норм ли то, что за первую попытку начисляют меньше очков
    // может быть переделать этот момент
    static int countLuckyPoints(int wordLen, LetterCounters lettersCounter) {
        return countPoints(1, lettersCounter) / wordLen;
    }

}
