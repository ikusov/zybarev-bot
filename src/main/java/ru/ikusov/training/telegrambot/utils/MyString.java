package ru.ikusov.training.telegrambot.utils;

public final class MyString {
    private MyString() {}

    public static int brutalParseInt(char[] chars) throws NumberFormatException {
        char first = chars[0];
        if (first!='-' && !Character.isDigit(first))
            throw new NumberFormatException();

        final int INT_MAX_LEN = String.valueOf(Integer.MAX_VALUE).length(),
                    LONG_MAX_LEN = String.valueOf(Long.MAX_VALUE).length();

        int len = chars.length, count=0;
        char[] number = new char[chars.length];

        for (int i=0; i<len && count<INT_MAX_LEN; i++) {
            if (i==0 && chars[i]=='-' || Character.isDigit(chars[i])) {
                number[count++] = chars[i];
            }
        }

        return Integer.parseInt(String.valueOf(number).substring(0, count));
    }

    public static int brutalParseInt(String s) {
        return brutalParseInt(s.strip().toCharArray());
    }
}
