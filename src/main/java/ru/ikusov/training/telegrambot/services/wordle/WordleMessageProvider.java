package ru.ikusov.training.telegrambot.services.wordle;

import static ru.ikusov.training.telegrambot.utils.Linguistic.getPointWordEndingIme;
import static ru.ikusov.training.telegrambot.utils.MyString.markdownv2Format;

public final class WordleMessageProvider {
    private WordleMessageProvider() {}
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

    private static String m(String s) {
        return markdownv2Format(s);
    }
}
