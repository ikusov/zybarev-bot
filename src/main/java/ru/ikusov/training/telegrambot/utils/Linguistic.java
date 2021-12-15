package ru.ikusov.training.telegrambot.utils;

public final class Linguistic {
    private Linguistic(){}

    public static String getFemaleWordEnding(int num) {
        int r10 = num%10,
                r100 = num%100;
        String ending = r100 == 1 ? "а" :
                r100>1 && r100<5 ? "ы" :
                r100>=5 && r100<21 ? "" :
                r10 == 1 ? "а" :
                r10>1 && r10<5 ? "ы" :
                "";
        return ending;
    }

    public static String getMaleWordEnding(int num) {
        int r10 = num%10,
                r100 = num%100;
        String ending =
                r100>1 && r100<5 ? "а" :
                        r100>=5 && r100<21 ? "ов" :
                                r10 == 1 ? "" :
                                        r10>1 && r10<5 ? "а" :
                                                "ов";
        return ending;
    }
}
