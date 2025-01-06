package ru.ikusov.training.telegrambot.services.wordle;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import ru.ikusov.training.telegrambot.TestUtils;

import java.io.IOException;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.params.provider.Arguments.arguments;

class WiktionaryWordCheckerOLDTest {
    private final WiktionaryWordChecker_OLD wordChecker = new WiktionaryWordChecker_OLD();

    @ParameterizedTest
    @MethodSource
    void check(String word, boolean expectedResult) throws IOException {
        TestUtils.delayRandomTime(1000, 5000);
        boolean actualResult = wordChecker.check(word);

        assertEquals(expectedResult, actualResult);
    }

    public static Stream<Arguments> check() {
        return Stream.of(
                arguments("борьба", true),
                arguments("пиво", true),
                arguments("пресс", true),
                arguments("героиня", true),
                arguments("мельница", true),
                arguments("наполеон", true),
                arguments("сидеть", false)
        );
    }

 }