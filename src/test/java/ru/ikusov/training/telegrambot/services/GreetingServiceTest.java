package ru.ikusov.training.telegrambot.services;

import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.ikusov.training.telegrambot.utils.MessageType;

import java.time.Clock;
import java.time.Instant;
import java.time.ZoneId;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.params.provider.Arguments.of;

@ExtendWith(MockitoExtension.class)
class GreetingServiceTest {

    public static Stream<Arguments> greetingMessage() {
        return Stream.of(
                of(Instant.parse("2020-12-20T00:00:00.0Z"), MessageType.GREETING_MESSAGE.getRandomMessage()),
                of(Instant.parse("2020-12-30T00:00:00.0Z"), MessageType.GREETING_MESSAGE.getRandomMessage()),
                of(Instant.parse("2020-01-01T00:00:00.0Z"), MessageType.GREETING_MESSAGE.getRandomMessage()),
                of(Instant.parse("2020-01-09T00:00:00.0Z"), MessageType.GREETING_MESSAGE.getRandomMessage()),
                of(Instant.parse("2020-01-14T00:00:00.0Z"), MessageType.GREETING_MESSAGE.getRandomMessage()),
                of(Instant.parse("2020-02-20T00:00:00.0Z"), MessageType.HNY_GREETING_MESSAGE.getRandomMessage()),
                of(Instant.parse("2020-03-20T00:00:00.0Z"), MessageType.HNY_GREETING_MESSAGE.getRandomMessage()),
                of(Instant.parse("2020-04-20T00:00:00.0Z"), MessageType.HNY_GREETING_MESSAGE.getRandomMessage()),
                of(Instant.parse("2020-05-20T00:00:00.0Z"), MessageType.HNY_GREETING_MESSAGE.getRandomMessage()),
                of(Instant.parse("2020-06-20T00:00:00.0Z"), MessageType.HNY_GREETING_MESSAGE.getRandomMessage()),
                of(Instant.parse("2020-07-20T00:00:00.0Z"), MessageType.HNY_GREETING_MESSAGE.getRandomMessage()),
                of(Instant.parse("2020-08-20T00:00:00.0Z"), MessageType.HNY_GREETING_MESSAGE.getRandomMessage()),
                of(Instant.parse("2020-09-20T00:00:00.0Z"), MessageType.HNY_GREETING_MESSAGE.getRandomMessage()),
                of(Instant.parse("2020-10-20T00:00:00.0Z"), MessageType.HNY_GREETING_MESSAGE.getRandomMessage()),
                of(Instant.parse("2020-11-20T00:00:00.0Z"), MessageType.HNY_GREETING_MESSAGE.getRandomMessage())
        );
    }

    @ParameterizedTest
    @MethodSource
    void greetingMessage(Instant theMoment, String unexpectedMessage) {
        Clock mockedClock = Clock.fixed(theMoment, ZoneId.systemDefault());
        GreetingService sut = new GreetingService(mockedClock);

        String actual = sut.greetingMessage();

        assertNotEquals(unexpectedMessage, actual);
    }
}