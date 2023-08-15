package ru.ikusov.training.telegrambot.services.wordle;

import lombok.experimental.UtilityClass;
import ru.ikusov.training.telegrambot.services.wordle.dto.WordleChatStatDto;
import ru.ikusov.training.telegrambot.services.wordle.dto.WordleStartGameDto;

import static ru.ikusov.training.telegrambot.utils.Linguistic.getPointWordEndingIme;
import static ru.ikusov.training.telegrambot.utils.MyString.markdownv2Format;

@UtilityClass
public class WordleMessageProvider {
    public static String formattedUserPointsMessage(String userName, int currentPoints, int sumPoints) {
        String currentPointsWord = "очк" + getPointWordEndingIme(currentPoints);
        String sumPointsWord = "очк" + getPointWordEndingIme(sumPoints);

        return "\n" + m("Пользователь ") +
                "*" + m(userName) + "*" +
                m(" получает ") +
                "*" + currentPoints + "*" +
                m(" " + currentPointsWord + " за правильный ответ!") + "\n" +
                m("Всего у пользователя ") +
                "*" + m(userName) + "* " +
                "*" + sumPoints + "*" +
                m(" " + sumPointsWord + "!");
    }

    public static String formattedStatMessage(WordleChatStatDto wordleChatStatDto) {
        return "EMPTY_FORMATTED_STAT_MESSAGE";
    }

    public static String formattedStartGameMessage(WordleStartGameDto wordleStartGameDto) {
        return "EMPTY_formattedStartGameMessage";
    }

    private static String m(String s) {
        return markdownv2Format(s);
    }
}
