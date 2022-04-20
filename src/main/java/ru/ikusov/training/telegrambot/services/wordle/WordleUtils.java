package ru.ikusov.training.telegrambot.services.wordle;

import ru.ikusov.training.telegrambot.utils.MyString;

public final class WordleUtils {
    private WordleUtils() {
    }

    public static int[] compareWords(String tested, String right) {
        int[] result = new int[tested.length()];
        char[] t = tested.toCharArray();
        char[] r = right.toCharArray();

        int[] lettersCounter = new int[Math.max('ё', 'Ё') - 'А'];

        for (char c : r) {
            int lcI = c - 'А';
            lettersCounter[lcI]++;
        }

        for (int i = 0; i < t.length; i++) {
            int lcI = t[i] - 'А';
            if (t[i] == r[i]) {
                result[i] = 2;
                lettersCounter[lcI]--;
            } else {
                result[i] = 0;
            }
        }

        for (int i = 0; i < t.length; i++) {
            int lcI = t[i] - 'А';
            if (result[i] != 2 && lettersCounter[lcI] > 0) {
                result[i] = 1;
                lettersCounter[lcI]--;
            }
        }

//        for (int i = 0; i < t.length; i++) {
//            if (result[i] == 2) continue;
//
//            int ind = right.indexOf(t[i]);
//            result[i] = ind >= 0 && result[ind] != 2
//                    ? 1
//                    : 0;
//        }
//
//
//        for (int i = t.length - 1; i >= 0; i--) {
//            if (result[i] == 1 && countOnes(result) > MyString.countChars(right, t[i])) {
//                result[i] = 0;
//            }
//        }
//
        return result;
    }

    public static boolean isFullOfTwos(int[] numbers) {
        for (int n : numbers) {
            if (n != 2) return false;
        }
        return true;
    }

    public static int countOnes(int[] numbers) {
        int count = 0;
        for (int num : numbers) {
            if (num == 1) count++;
        }

        return count;
    }

    public static String formatToMarkdownV2(String s, int[] wordComparingResult) {
        StringBuilder sb = new StringBuilder(s.length() * 5);
        for (int i = 0; i < s.length(); i++) {
            var c = Character.toUpperCase(s.charAt(i));
            sb.append(
                    wordComparingResult[i] == 2
                            ? "*" + c + "*"
                            : wordComparingResult[i] == 1
                            ? "_" + c + "_\r"
                            : "~" + c + "~"
            );
        }

        return sb.toString();
    }

    public static boolean isWordleAnswer(String input) {
        if (input.length() != 5) return false;
        char[] chars = input.toCharArray();

        for (char c : chars) {
            if ((c < 'А' || c > 'я') && (c != 'ё' && c != 'Ё')) return false;
        }

        return true;
    }
}
