package ru.ikusov.training.telegrambot.services.wordle;

import java.util.List;

import static ru.ikusov.training.telegrambot.services.wordle.WordleUtils.compareWords;
import static ru.ikusov.training.telegrambot.utils.CommonUtils.countTrues;
import static ru.ikusov.training.telegrambot.utils.Linguistic.RUSSIAN_ALPHABET_LETTERS_COUNT;
import static ru.ikusov.training.telegrambot.utils.Linguistic.calculateRussianLetterAlphabetIndex;

public final class WordlePointsCalculator {
    private WordlePointsCalculator() {}

    public static int countPoints(int wordLen, int guessedLettersCount, int placedLettersCount, int leftLetters) {
        int BI = 1000;
        int MAX_WORD_POINTS = 12 + wordLen;
        int MP = BI * MAX_WORD_POINTS;

        int multiPoints = (
                MP * guessedLettersCount * leftLetters / RUSSIAN_ALPHABET_LETTERS_COUNT
                + MP * placedLettersCount / 2
        ) / wordLen;

        return multiPoints < 1000
                ? 1
                : multiPoints / 1000;
    }

    public static int countPoints(List<String> triedWords) {
        boolean[] triedLetters = new boolean[RUSSIAN_ALPHABET_LETTERS_COUNT];
        boolean[] movedLetters;
        boolean[] guessedLetters;

        var lastIndex = triedWords.size() - 1;
        var rightWord = triedWords.get(lastIndex);
        var attemptWords = triedWords.subList(0, lastIndex);
        var wordLen = rightWord.length();
        guessedLetters = new boolean[wordLen];
        movedLetters = new boolean[wordLen];

        for (String testedWord : attemptWords) {
            var compareResult = compareWords(testedWord, rightWord);
            for (int i = 0; i < wordLen; i++) {
                if (compareResult[i] == 2) {
                    guessedLetters[i] = true;
                }
                char letter = testedWord.toCharArray()[i];
                int letterIndex = calculateRussianLetterAlphabetIndex(letter);
                if (compareResult[i] != 1) {
                    triedLetters[letterIndex] = true;
                }
            }
        }

        for (String testedWord : attemptWords) {
            var compareResult = compareWords(testedWord, rightWord);
            for (int i = 0; i < wordLen; i++) {
                if (compareResult[i] == 1 && !guessedLetters[i]) {
                    movedLetters[i] = true;
                }
            }
        }

        return countPoints(
                wordLen,
                wordLen - countTrues(guessedLetters) - countTrues(movedLetters),
                countTrues(movedLetters),
                RUSSIAN_ALPHABET_LETTERS_COUNT - countTrues(triedLetters)
        );
    }
}
