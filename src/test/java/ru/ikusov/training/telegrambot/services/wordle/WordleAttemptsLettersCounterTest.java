package ru.ikusov.training.telegrambot.services.wordle;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import ru.ikusov.training.telegrambot.services.wordle.WordleAttemptsLettersCounter.LetterCounters;

import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.params.provider.Arguments.of;

class WordleAttemptsLettersCounterTest {

    @ParameterizedTest(name = "{0}")
    @MethodSource
    void countLetters(String testDescription,
                      String rightWord,
                      String currentWord,
                      List<String> triedWords,
                      LetterCounters expectedResult) {
        LetterCounters actualResult =
                WordleAttemptsLettersCounter.countLetters(rightWord, currentWord, triedWords);

        assertEquals(expectedResult.guessedLettersCount(), actualResult.guessedLettersCount());
        assertEquals(expectedResult.placedLettersCount(), actualResult.placedLettersCount());
        assertEquals(expectedResult.knownLettersCount(), actualResult.knownLettersCount());
        assertEquals(expectedResult.leftLettersCount(), actualResult.leftLettersCount());
    }

    //TODO: разделить этот источник на несколько по каким-то критериям
    // подумать ещё над тесткейсами, а то чё-то внезапно баги вылазят с каждым новым кейсом
    private static Stream<Arguments> countLetters() {
        return Stream.of(
                of("1 Слово 8, угаданных ранее 0. Угадано 0, угадано известных 0, стала известной 0",
                        "аааабббб",
                        "ггггагаа",
                        List.of("вввввааа"),
                        new LetterCounters(0, 0, 0, 31))

                , of("2 Слово 8, угаданных ранее 0. Угадано 0, угадано известных 0, стала известной 1",
                        "аааабббб",
                        "ггггаааа",
                        List.of("вввввааа"),
                        new LetterCounters(0, 0, 1, 31))

                , of("3 Слово 8, угаданных ранее 1. Угадано 0, угадано известных 0, стала известной 1",
                        "аааабббб",
                        "ггггаааа",
                        List.of("вввваааб"),
                        new LetterCounters(0, 0, 1, 30))

                , of("4 Слово 8, угаданных ранее 1. Угадано 0, угадано известных 1, стала известной 0",
                        "аааабббб",
                        "гггааагб",
                        List.of("вввваааб"),
                        new LetterCounters(0, 1, 0, 30))

                , of("5 Слово 8, угаданных ранее 1. Угадано 1, угадано известных 1, стала известной 0",
                        "аааабббб",
                        "гггааабб",
                        List.of("вввваааб"),
                        new LetterCounters(1, 1, 0, 30))

                , of("6 Слово 8, угаданных ранее 1. Угадано 0, угадано известных 1, стала известной 1",
                        "аааабббб",
                        "гггааагб",
                        List.of("вввваавб"),
                        new LetterCounters(0, 1, 1, 30))

                , of("7 Слово 8, угаданных ранее 1. Угадано 1, угадано известных 1, стала известной 1",
                        "аааабббб",
                        "гггааабб",
                        List.of("вввваавб"),
                        new LetterCounters(1, 1, 1, 30))

                , of("8 Слово 8, угаданных ранее 2. Угадано 1, угадано известных 1, стала известной 1",
                        "аааабббб",
                        "гггааабб",
                        List.of("вввваавб", "ддддбддд"),
                        new LetterCounters(1, 1, 1, 29))

                , of("9 Слово 8, угаданных ранее 2. Угадано 0, угадано известных 0, стала известной 0",
                        "аааабббб",
                        "гггааабб",
                        List.of("гггааабб", "вввваавб", "гггааабб", "ддддбддд"),
                        new LetterCounters(0, 0, 0, 28))

                , of("10 Слово 8, угаданных ранее 2. Угадано 0, угадано известных 1, стала известной 1",
                        "аааабббб",
                        "ггаааабб",
                        List.of("гггааабб", "вввваавб", "гггааабб", "ддддбддд"),
                        new LetterCounters(0, 1, 1, 28))

                , of("11 Слово 12, угаданных ранее 12. Угадано 0, угадано известных 0, стала известной 0",
                        "ааааббббвввв",
                        "вввваааабббб",
                        List.of("гггабггггггв", "ггагггбгггвг", "гагггбгггвгг", "аггбвггбвгга"),
                        new LetterCounters(0, 0, 0, 29))

                , of("12 Слово 12, угаданных ранее 5. Угадано 0, угадано известных 0, стала известной 1",
                        "гагагагагага",
                        "цацацацаацца",
                        List.of(
                                "угуфуфуфуфуф",
                                "стстстсастат",
                                "прпрпапрпрпр",
                                "нонанонононо",
                                "лалмлмлмлмлм",
                                "йкйкакакйкйа",
                                "ыжыжыжыжажаж",
                                "адедедедедед",
                                "изазизизизиа",
                                "изазизизизиа",
                                "ыжыжыжыжажаж",
                                "адедедедедед",
                                "бвбвбвбвбвбв"
                        ),
                        new LetterCounters(0, 0, 1, 11))
        );
    }

}