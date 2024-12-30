package ru.ikusov.training.telegrambot.services.wordle;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.*;

import static java.lang.Math.max;
import static ru.ikusov.training.telegrambot.services.wordle.WordleUtils.compareWords;
import static ru.ikusov.training.telegrambot.utils.Linguistic.RUSSIAN_ALPHABET_LOWERCASE_SET;

/**
 * <p>Класс, содержащий логику проверки изменения статуса букв слова в текущей попытки отгадывания
 * слова.</p>
 * <p>Статусы букв попытки отгадывания слова выбраны следующие:</p>
 * <ol>
 * <li>
 *     <b>Угаданная буква</b> - буква, наличие которой не было известно до момента текущей попытки
 *     и которая была угадана в данной попытке
 * </li>
 * <li>
 *     <b>Угаданная известная буква</b> - буква, наличие которой было известно до момента текущей попытки
 *     и которая была угадана в данной попытке
 * </li>
 * <li>
 *     <b>Известная буква</b> - буква, наличие которой в слове не было известно до момента текущей попытки, но
 *     было подтверждено в данной попытке
 * </li>
 * <li>
 *     <b>"Оставшиеся" буквы</b> - буквы русского алфавита, наличие или отсутствие которых в слове не было выяснено
 *     в предыдущих попытках
 * </li>
 * </ol>
 */
@Slf4j
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class WordleAttemptsLettersCounter {
    public static LetterCounters countLetters(String rightWord, String currentWord, List<String> triedWords) {
        int wordLen = rightWord.length();

        //<editor-fold desc="Собираем буквы, угаданные в предыдущих попытках и буквы, проверенные в предыдущих попытках">
        //буквы алфавита, не проверенные в предыдущих попытках
        Set<Character> leftLetters = new HashSet<>(RUSSIAN_ALPHABET_LOWERCASE_SET);
        //позиции букв, угаданных в предыдущих попытках
        boolean[] isEarlyGuessedLetter = new boolean[wordLen];
        //набор букв, угаданных в предыдущих попытках
        Map<Character, Integer> earlyGuessedLetters = new HashMap<>();

        collectEarlyGuessedAndLeftLetters(rightWord,
                triedWords,
                leftLetters,
                isEarlyGuessedLetter,
                earlyGuessedLetters);

        log.debug("Массив признаков угаданности букв в предыдущих попытках: {}", isEarlyGuessedLetter);
        log.debug("Буквы, угаданные в предыдущих попытках: {}", earlyGuessedLetters);
        //</editor-fold>

        //<editor-fold desc="Собираем буквы, ставшие известными в предыдущих попытках (угадана буква, но не её позиция)">
        //набор букв, ставших известными в предыдущих попытках
        Map<Character, Integer> earlyKnownLetters = new HashMap<>();

        collectEarlyKnownLetters(rightWord, triedWords, earlyGuessedLetters, isEarlyGuessedLetter, earlyKnownLetters);
        //</editor-fold>

        //теперь нужно поработать по текущей попытке (за которую начисляем очки)

        //<editor-fold desc="Собираем буквы по текущей попытке (угаданные, угаданные из известных и известные)">
        //буквы, угаданные в данной попытке
        Map<Character, Integer> currentGuessedLetters = new HashMap<>();
        //буквы, угаданные из известных в данной попытке
        Map<Character, Integer> currentPlacedLetters = new HashMap<>();
        //буквы, ставшие известными в данной попытке
        Map<Character, Integer> currentKnownLetters = new HashMap<>();

        //пробегаемся по статусам букв текущего слова
        //для начала вычисляем статусы букв текущей попытки
        collectCurrentLetters(rightWord,
                currentWord,
                isEarlyGuessedLetter,
                earlyKnownLetters,
                currentGuessedLetters,
                currentPlacedLetters,
                currentKnownLetters);

        log.debug("Буквы, ставшие известными в предыдущих попытках: {}", earlyKnownLetters);
        log.debug("Буквы, угаданные в данной попытке: {}", currentGuessedLetters);
        log.debug("Буквы, угаданные из известных в данной попытке: {}", currentPlacedLetters);
        //</editor-fold>

        //<editor-fold desc="Окончательно формируем набор букв, ставших известными в текущей попытке">
        collectCurrentKnownLetters(earlyGuessedLetters,
                earlyKnownLetters,
                currentGuessedLetters,
                currentPlacedLetters,
                currentKnownLetters);
        log.debug("Буквы, известные после данной попытки: {}", currentKnownLetters);
        //</editor-fold>

        //посчитаем количества букв
        //количество буков, прям угаданных в текущей попытке
        int guessedLettersCount = sumPositiveValues(currentGuessedLetters);

        //количество буков, бывших известными ранее, но чьи позиции были угаданы в данной попытке
        int placedLettersCount = sumPositiveValues(currentPlacedLetters);

        //количество буков, ставших известными в данной попытке
        int knownLettersCount = sumPositiveValues(currentKnownLetters);

        LetterCounters letterCounters =
                new LetterCounters(guessedLettersCount, placedLettersCount, knownLettersCount, leftLetters.size());
        log.debug("Для слова {} посчитаны количества букв: {}", currentWord, letterCounters);

        return letterCounters;
    }

    private static void collectCurrentKnownLetters(Map<Character, Integer> earlyGuessedLetters,
                                                   Map<Character, Integer> earlyKnownLetters,
                                                   Map<Character, Integer> currentGuessedLetters,
                                                   Map<Character, Integer> currentPlacedLetters,
                                                   Map<Character, Integer> currentKnownLetters) {
        //посчитаем буквы, ставшие известными в данной попытке
        // (нужно из списка известных в данной попытке букв убрать известные ранее и угаданные)
        //пробегаемся по статусам букв текущего слова
        for (var letter : currentKnownLetters.keySet()) {
            currentKnownLetters.computeIfPresent(
                    letter,
                    (let, count) ->
                            max(
                                    0,
                                    count
                                            - earlyKnownLetters.getOrDefault(letter, 0)
                                            - earlyGuessedLetters.getOrDefault(letter, 0)
                                            - currentGuessedLetters.getOrDefault(letter, 0)
                                            - currentPlacedLetters.getOrDefault(letter, 0)
                            )
            );
        }
    }

    private static void collectCurrentLetters(String rightWord,
                                              String currentWord,
                                              boolean[] isEarlyGuessedLetter,
                                              Map<Character, Integer> earlyKnownLetters,
                                              Map<Character, Integer> currentGuessedLetters,
                                              Map<Character, Integer> currentPlacedLetters,
                                              Map<Character, Integer> currentKnownOrGuessedLetters) {
        int[] currentLettersStatuses = compareWords(currentWord, rightWord);
        for (int i = 0; i < currentLettersStatuses.length; i++) {
            Character letter = currentWord.charAt(i);

            //Если статус 2 (т.е. угадана)
            //и не была угадана ранее
            if (currentLettersStatuses[i] == 2 && !isEarlyGuessedLetter[i]) {
                if (earlyKnownLetters.getOrDefault(letter, 0) == 0) {
                    //если не была известна ранее
                    //складываем букву в мапу ныне угаданных с нуля буков
                    currentGuessedLetters.merge(letter, 1, (ov, v) -> ov + 1);
                } else {
                    //если была известна ранее
                    //добавляем букву в мапу ныне угаданных из известных
                    currentPlacedLetters.merge(letter, 1, (c, c1) -> c + 1);
                    //удаляем из известных ранее
                    earlyKnownLetters.computeIfPresent(letter, (ltr, cnt) -> max(0, cnt - 1));
                }
            }

            //складываем буквы, известные либо угаданные в данной попытке
            // (без учёта того, были ли они известны либо угаданы ранее)
            if (currentLettersStatuses[i] != 0) {
                currentKnownOrGuessedLetters.merge(letter, 1, (c, c1) -> c + 1);
            }
        }
    }

    private static void collectEarlyKnownLetters(String rightWord,
                                                 List<String> triedWords,
                                                 Map<Character, Integer> earlyGuessedLetters,
                                                 boolean[] isEarlyGuessedLetter,
                                                 Map<Character, Integer> earlyKnownLetters) {
        for (String triedWord : triedWords) {
            //мапка для букв, которые были угаданы или стали известными на данной итерации
            Map<Character, Integer> triedWordKnownLetters = new HashMap<>();
            int[] result0 = compareWords(triedWord, rightWord);
            for (int i = 0; i < result0.length; i++) {
                if (result0[i] != 0) {
                    Character letter1 = triedWord.charAt(i);
                    Integer count = triedWordKnownLetters.getOrDefault(letter1, 0) + 1;
                    triedWordKnownLetters.put(letter1, count);
                }
            }

            for (var letterMapping : triedWordKnownLetters.entrySet()) {
                var letter = letterMapping.getKey();
                var letterCount = letterMapping.getValue();
                earlyKnownLetters.computeIfPresent(letter, (l, c) -> max(c, letterCount));
                earlyKnownLetters.computeIfAbsent(letter, k -> letterCount);
            }
        }

        //нужно удалить из набора известных букв полноценно угаданные
//        for (int i = 0; i < isEarlyGuessedLetter.length; i++) {
//            if (!isEarlyGuessedLetter[i]) {
//                continue;
//            }
//            var letter = rightWord.charAt(i);
//            //если буква была известна ранее, то убавим количество
//            earlyKnownLetters.computeIfPresent(letter, (l, c) -> max(0, c - 1));
//        }

        //TODO: проверить другой спосог
        // смысл: пытаемся избавиться от использования isEarlyGuessedLetter в дальнейшем
        // чтобы вынести логику наполнения мапок в отдельные методы
        for (var lm : earlyGuessedLetters.entrySet()) {
            Character letter = lm.getKey();
            Integer count = lm.getValue();

            earlyKnownLetters.computeIfPresent(letter, (l, c) -> max(0, c - count));
        }
    }

    private static void collectEarlyGuessedAndLeftLetters(String rightWord,
                                                          List<String> triedWords,
                                                          Set<Character> leftLetters,
                                                          boolean[] isEarlyGuessedLetter,
                                                          Map<Character, Integer> earlyGuessedLetters) {
        for (String triedWord : triedWords) {
            int[] statuses = compareWords(triedWord, rightWord);
            for (int i = 0; i < statuses.length; i++) {
                if (statuses[i] == 2 && !isEarlyGuessedLetter[i]) {
                    //добавляем букву в мапу угаданных
                    earlyGuessedLetters.merge(triedWord.charAt(i), 1, (c, c1) -> c + 1);
                    isEarlyGuessedLetter[i] = true;
                }
                //убираем букву из оставшихся
                leftLetters.remove(triedWord.charAt(i));
            }
        }
    }

    static <T> int sumPositiveValues(Map<T, Integer> map) {
        return map.values().stream().mapToInt(v -> v).filter(v -> v > 0).sum();
    }

    record LetterCounters(
            int guessedLettersCount,
            int placedLettersCount,
            int knownLettersCount,
            int leftLettersCount) {
    }
}
