package ru.ikusov.training.telegrambot.utils;

import java.util.Map;

public final class Linguistic {
    public static final Map<Integer, String> russianNumberNamesGenetive =
            Map.ofEntries(
              Map.entry(2, "двух"),
              Map.entry(3, "трёх"),
              Map.entry(4, "четырёх"),
              Map.entry(5, "пяти"),
              Map.entry(6, "шести"),
              Map.entry(7, "семи"),
              Map.entry(8, "восьми"),
              Map.entry(9, "девяти"),
              Map.entry(10, "десяти")
            );
    private static final String[] cutletWordEndingsIme = {"а", "ы", ""};
    private static final String[] cutletWordEndingsVin = {"у", "ы", ""};
    private static final String[] manulWordEndingsIme = {"", "а", "ов"};

    private Linguistic(){}

    public static String getCutletWordEnding(int num) {
        int r10 = num%10,
            r100 = num%100;
        return r100 == 1 ? "а" :
                r100>1 && r100<5 ? "ы" :
                r100>=5 && r100<21 ? "" :
                r10 == 1 ? "а" :
                r10>1 && r10<5 ? "ы" :
                "";
    }

    public static String getManulWordEnding(int num) {
        int r10 = num%10,
            r100 = num%100;
        return r100>1 && r100<5 ? "а" :
               r100>=5 && r100<21 ? "ов" :
               r10 == 1 ? "" :
               r10>1 && r10<5 ? "а" :
               "ов";
    }

    public static String getCutletWordEndingIme(int num) {
        return getWordEnding(num, cutletWordEndingsIme);
    }

    public static String getCutletWordEndingVin(int num) {
        return getWordEnding(num, cutletWordEndingsVin);
    }

    public static String getManulWordEndingIme(int num) {
        return getWordEnding(num, manulWordEndingsIme);
    }

    public static String getWordEnding(int num, String[] endings) {
        int r10 = num%10,
                r100 = num%100;
        return r100>1 && r100<5 ? endings[1] :
                r100>=5 && r100<21 ? endings[2] :
                        r10 == 1 ? endings[0] :
                                r10>1 && r10<5 ? endings[1] :
                                        endings[2];
    }

    public static String getSevereWordEnding(int _case) {
        return    _case==0 ? "ый"
                : _case==1 ? "ого"
                : _case==2 ? "ому"
                : _case==3 ? "ый"
                : _case==4 ? "ым"
                :            "ом";
    }

    public enum CASE {
        IME,
        ROD,
        DAT,
        VIN,
        TVO,
        PRE
    }
}
