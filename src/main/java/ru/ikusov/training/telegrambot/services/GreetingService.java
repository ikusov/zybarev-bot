package ru.ikusov.training.telegrambot.services;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.ikusov.training.telegrambot.utils.MessageType;

import java.time.Clock;
import java.time.LocalDate;
import java.time.Month;

@Component
@RequiredArgsConstructor
public class GreetingService {
    private static final LocalDate BEFORE_NY = LocalDate.of(2000, Month.DECEMBER, 15);
    private static final LocalDate AFTER_NY = LocalDate.of(2000, Month.JANUARY, 15);

    private final Clock clock;

    public String greetingMessage() {
        LocalDate theMoment = LocalDate.now(clock);
        int theMomentYear = theMoment.getYear();

        LocalDate startNyGreetings = LocalDate.of(theMomentYear, BEFORE_NY.getMonth(), BEFORE_NY.getDayOfMonth());
        LocalDate endNyGreetings = LocalDate.of(theMomentYear, AFTER_NY.getMonth(), AFTER_NY.getDayOfMonth());

        return theMoment.isAfter(startNyGreetings) || theMoment.isBefore(endNyGreetings)
                ? MessageType.HNY_GREETING_MESSAGE.getRandomMessage()
                : MessageType.GREETING_MESSAGE.getRandomMessage();
    }
}
