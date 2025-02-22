package ru.ikusov.training.telegrambot.services.wordle;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.ikusov.training.telegrambot.TestUtils;
import ru.ikusov.training.telegrambot.services.wordle.cache.ExistingWordsCache;

import java.io.IOException;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.params.provider.Arguments.arguments;
import static org.mockito.Mockito.doNothing;

@ExtendWith(MockitoExtension.class)
class YandexWordCheckerTest {
    @Mock
    private ExistingWordsCache existingWordsCache;

    @InjectMocks
    private YandexWordChecker wordChecker;

    @Disabled
    @ParameterizedTest
    @MethodSource
    void checkExisting(String word) throws IOException {
        doNothing().when(existingWordsCache).add(word);
        TestUtils.delayRandomTime(1000, 5000);

        boolean actualResult = wordChecker.check(word);

        assertTrue(actualResult);
    }

    public static Stream<Arguments> checkExisting() {
        return Stream.of(
                arguments("борьба"),
                arguments("пиво"),
                arguments("пресс"),
                arguments("героиня"),
                arguments("мельница"),
                arguments("наполеон")
        );
    }

    @Disabled
    @ParameterizedTest
    @MethodSource
    void checkNotExisting(String word) throws IOException {
        TestUtils.delayRandomTime(1000, 5000);

        boolean actualResult = wordChecker.check(word);

        assertFalse(actualResult);
    }

    public static Stream<Arguments> checkNotExisting() {
        return Stream.of(
                arguments("гудеть"),
                arguments("шопотом"),
                arguments("отрадно"),
                arguments("дровец"),
                arguments("сидеть")
        );
    }

}