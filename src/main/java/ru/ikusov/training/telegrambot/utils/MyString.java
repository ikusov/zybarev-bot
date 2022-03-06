package ru.ikusov.training.telegrambot.utils;

import java.util.Locale;
import java.util.Set;

import static ru.ikusov.training.telegrambot.MainClass.RUS_LOCALE;

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

    public static int brutalParseInt(String s) throws NumberFormatException {
        return brutalParseInt(s.strip().toCharArray());
    }

    public static String trimPunctuationMarksInclusive(String string) {
        Set<Character> punctuationMarksSet = Set.of('!', '.', '1');
        char[] chars = string.toCharArray();
        int start = 0, end = string.length()-1;
        while (start<end && (punctuationMarksSet.contains(chars[start]) || Character.isWhitespace(chars[start])))
            start ++;
        while (start<end && (punctuationMarksSet.contains(chars[end]) || Character.isWhitespace(chars[end])))
            end --;

        return start<end ? string.substring(start, end+1) : "";
    }

    public static String brutalProcessing(String string) {
        Set<Character> punctuationMarksSet = Set.of(',', '!', '.', '1', '"', '?');
        char[] chars = string.toLowerCase(RUS_LOCALE).replaceAll("ё", "е").toCharArray();
        StringBuilder sb = new StringBuilder();
        for (char c:chars) {
            if (punctuationMarksSet.contains(c) || Character.isWhitespace(c) || Character.isDigit(c))
                continue;
            sb.append(c);
        }
        return sb.toString();
    }

    public static String markdownv2Format(String input) {
        char[] inputChars = input.toCharArray();
        StringBuilder output = new StringBuilder();

        for (char c : inputChars) {
            if (c-1<126)
                output.append('\\');
            output.append(c);
        }

        return output.toString();
    }
}
