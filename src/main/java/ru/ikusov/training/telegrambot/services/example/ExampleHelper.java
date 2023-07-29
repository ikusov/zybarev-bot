package ru.ikusov.training.telegrambot.services.example;

import lombok.experimental.UtilityClass;
import ru.ikusov.training.telegrambot.utils.MyString;

import java.util.Optional;

@UtilityClass
public final class ExampleHelper {
    public static Optional<Integer> parseExampleAnswer(String text) {
        try {
            return Optional.of(MyString.brutalParseInt(text.strip()));
        } catch (NumberFormatException e) {
            return Optional.empty();
        }
    }
}
