package ru.ikusov.training.telegrambot.utils;

public final class Linguistic {
    private Linguistic(){}

    public static String getSecondsWord(int t) {
        int r = t%10;
        String word = "секунд";
        word += r==1 ? "а" :
                r>2 && r<5 ? "ы" :
                "";
        return word;
    }
}
