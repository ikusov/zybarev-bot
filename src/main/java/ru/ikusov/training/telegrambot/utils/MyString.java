package ru.ikusov.training.telegrambot.utils;

import java.util.List;
import java.util.Objects;
import java.util.Set;

import static ru.ikusov.training.telegrambot.MainClass.RUS_LOCALE;

public final class MyString {

    private static final List<Integer> RUSSIAN_ALPHABET_CODEPOINTS = "абвгдеёжзиклмнопрстуфхцчшщъыьэюя".codePoints().boxed().toList();

    private MyString() {
    }

    /**
     * Search string in stringList with length closest to stringLen. Returns it and removes it from stringList.
     * For stringLen <= 0 returns and removes first element of stringList.
     *
     * @param stringList list of strings
     * @param stringLen  length of searched string
     * @return first string with length closest to stringLen
     */
    public static String takeFromList(List<String> stringList, int stringLen) {
        int candidateIndex = 0;
        var curString = stringList.get(0);
        int minDif = Math.abs(stringLen - curString.length());
        if (minDif == 0 || stringLen <= 0) {
            stringList.remove(0);
            return curString;
        }

        for (int i = 1; i < stringList.size(); i++) {
            curString = stringList.get(i);
            var curDif = Math.abs(stringLen - curString.length());
            if (curDif < minDif) {
                minDif = curDif;
                candidateIndex = i;
            }
            if (curDif == 0) {
                break;
            }
        }

        var foundString = stringList.get(candidateIndex);
        stringList.remove(candidateIndex);
        return foundString;
    }

    public static int brutalParseInt(char[] chars) throws NumberFormatException {
        char first = chars[0];
        if (first != '-' && !Character.isDigit(first))
            throw new NumberFormatException();

        final int INT_MAX_LEN = String.valueOf(Integer.MAX_VALUE).length();

        int len = chars.length, count = 0;
        char[] number = new char[chars.length];

        for (int i = 0; i < len && count < INT_MAX_LEN; i++) {
            if (i == 0 && chars[i] == '-' || Character.isDigit(chars[i])) {
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
        int start = 0, end = string.length() - 1;
        while (start < end && (punctuationMarksSet.contains(chars[start]) || Character.isWhitespace(chars[start])))
            start++;
        while (start < end && (punctuationMarksSet.contains(chars[end]) || Character.isWhitespace(chars[end])))
            end--;

        return start < end ? string.substring(start, end + 1) : "";
    }

    public static String brutalProcessing(String string) {
        Set<Character> punctuationMarksSet = Set.of(',', '!', '.', '1', '"', '?');
        char[] chars = string.toLowerCase(RUS_LOCALE).replaceAll("ё", "е").toCharArray();
        StringBuilder sb = new StringBuilder();
        for (char c : chars) {
            if (punctuationMarksSet.contains(c) || Character.isWhitespace(c) || Character.isDigit(c))
                continue;
            sb.append(c);
        }
        return sb.toString();
    }

    public static String markdownv2Format(String input) {
        char[] inputChars = input.toCharArray();
        StringBuilder output = new StringBuilder(input.length() * 2);

        for (char c : inputChars) {
            if (c - 1 < 126)
                output.append('\\');
            output.append(c);
        }

        return output.toString();
    }

    /**
     * Приводит строку в формате markdownv2 в вид, пригодный для логирования.
     */
    public static String unmarkdownv2Format(String input) {
        char[] inputChars = input.toCharArray();
        int len = inputChars.length;

        StringBuilder output = new StringBuilder(len);

        for (int i = 0; i < len; i++) {
            if (isEscaping(inputChars, i)) {
            } else if (Set.of('\r', '\n').contains(inputChars[i])) {
                output.append(' ');
            } else {
                output.append(inputChars[i]);
            }
        }

        return output.toString();
    }

    /**
     * Возвращает признак того, что строки являются одинаковыми русскими словами.
     */
    public static boolean areEqualRussianWords(String string1, String string2) {
        if (string1 == null || string2 == null) {
            return false;
        }

        List<Integer> codepoints1 = getCodepointsFiltered(string1, RUSSIAN_ALPHABET_CODEPOINTS);
        List<Integer> codepoints2 = getCodepointsFiltered(string2, RUSSIAN_ALPHABET_CODEPOINTS);

        if (codepoints1.size() != codepoints2.size()) {
            return false;
        }

        for (int i = 0; i < codepoints1.size(); i++) {
            var char1 = codepoints1.get(i);
            var char2 = codepoints2.get(i);

            if (!Objects.equals(char1, char2)) {
                return false;
            }
        }

        return true;
    }

    private static List<Integer> getCodepointsFiltered(String string, List<Integer> filter) {
        return string.codePoints()
                .boxed()
                .filter(filter::contains)
                .toList();
    }

    private static boolean isEscaping(char[] inputChars, int i) {
        char ESCAPE = '\\';

        return inputChars[i] == ESCAPE && i < inputChars.length - 1 && inputChars[i + 1] - 1 < 126;
    }
}
