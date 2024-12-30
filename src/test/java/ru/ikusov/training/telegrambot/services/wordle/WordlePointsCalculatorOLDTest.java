package ru.ikusov.training.telegrambot.services.wordle;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import ru.ikusov.training.telegrambot.archive.wordle.WordlePointsCalculator_OLD;

import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.params.provider.Arguments.arguments;
import static ru.ikusov.training.telegrambot.utils.Linguistic.RUSSIAN_ALPHABET_LETTERS_COUNT;

class WordlePointsCalculatorOLDTest {
    @ParameterizedTest(name = "{1}")
    @MethodSource
    void countPoints(int expectedPoints, String triedWordsString) {
        var triedWords = List.of(triedWordsString.split(" "));
        var actualPoints = WordlePointsCalculator_OLD.countPoints(triedWords);

        assertEquals(expectedPoints, actualPoints);
    }

    private static Stream<Arguments> countPoints() {
        return Stream.of(
                arguments(13,   "пиво вино стул"),
                arguments(3,   "аавгб абвга"),
                arguments(7,   "вода соль горе мозг"),
                arguments(7,   "версия собака солдат"),
                arguments(9,   "гузель дорога радист снаряд"),
                arguments(16,   "пиво"),
                arguments(7,   "колбаса варенье семинар депутат"),
                arguments(4,    "хула море секс сейф сеть"),
                arguments(13,   "пиво вино вино вино вино пиво стул"),
                arguments(17,   "повод"),
                arguments(18,   "базука"),
                arguments(19,   "зыбарев"),
                arguments(20,   "сомнение"),
                arguments(10,   "есомнени сомнение"),
                arguments(4,   "полдник копилка копейка"),
                arguments(5,   "гора вата ламп дача каша"),
                arguments(6,   "грабли параша собака холера одежда метода"),
                arguments(11,   "рукопись государь"),
                arguments(5,   "работа бивень биение бизнес"),
                arguments(7,   "комар осень быдло вызов"),
                arguments(5,   "пиздец глицин глобус глоток"),
                arguments(5,   "вампир полоса педант печать"),
                arguments(9,   "марь тьма"),
                arguments(7,   "мешок кокон склон"),
                arguments(5,   "жопа каша вера тьма туча"),
                arguments(5,   "оглобля дофамин животик пилатес кипяток")
        );
    }
    @ParameterizedTest(name = "{5}")
    @MethodSource("countPoints2")
    void countPoints(int wordLen,
                     int guessedLettersCount,
                     int placedLettersCount,
                     int leftLettersCount,
                     int expectedPoints,
                     String name
    ) {
        var actualPoints = WordlePointsCalculator_OLD.countPoints(
                wordLen,
                guessedLettersCount,
                placedLettersCount,
                leftLettersCount
        );

        assertEquals(expectedPoints, actualPoints);
    }

    private static Stream<Arguments> countPoints2() {
        return Stream.of(
                arguments(5, 5, 0, RUSSIAN_ALPHABET_LETTERS_COUNT, 17, "Угадал сразу 5 бук"),
                arguments(4, 4, 0, RUSSIAN_ALPHABET_LETTERS_COUNT, 16, "Угадал сразу 4 букы"),
                arguments(7, 7, 0, RUSSIAN_ALPHABET_LETTERS_COUNT, 19, "Угадал сразу 7 бук"),
                arguments(7, 3, 2, RUSSIAN_ALPHABET_LETTERS_COUNT / 2, 6, "Угадал 3 переставил 2, оставалось полалфавита"),
                arguments(7, 3, 2, RUSSIAN_ALPHABET_LETTERS_COUNT * 2 / 3, 8, "Угадал 3 переставил 2, оставалось 2/3 алфавита"),
                arguments(7, 3, 2, RUSSIAN_ALPHABET_LETTERS_COUNT / 3, 5, "Угадал 3 переставил 2, оставалось 1/3 алфавита"),
                arguments(7, 0, 1, 1, 1, "Угадал одну переставленную"),
                arguments(7, 1, 0, 1, 1, "Осталас одна буква")
        );
    }
}