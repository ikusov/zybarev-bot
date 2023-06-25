package ru.ikusov.training.telegrambot.services.wordle;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.jsoup.select.Selector;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

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

        return result;
    }

    public static boolean isFullOfTwos(int[] numbers) {
        for (int n : numbers) {
            if (n != 2) return false;
        }
        return true;
    }

    static boolean isAlmostFullOfTwos(int[] compareResult) {
        if (compareResult == null) {
            return false;
        }

        int twosCount = 0;
        int onesCount = 0;
        for (int digit : compareResult) {
            if (digit == 2) {
                twosCount++;
            }
            if (digit == 1) {
                onesCount++;
            }
        }
        return twosCount == compareResult.length - 1
                || twosCount == compareResult.length - 2 && onesCount == 2;
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
        if (input.isEmpty()) {
            return false;
        }

        char[] chars = input.toCharArray();

        for (char c : chars) {
            if ((c < 'А' || c > 'я') && (c != 'ё' && c != 'Ё')) return false;
        }

        return true;
    }

    public static String toWordleString(String s) {
        return s.replaceAll("[ёЁ]", "е").toLowerCase();
    }

    public static boolean isWordExistsOnGramotaRu(String s) throws IOException {
        final String WORD_ABSENT_TEXT = "искомое слово отсутствует";
        final String url = "http://gramota.ru/slovari/dic/?lop=x&word=" + URLEncoder.encode(s, StandardCharsets.UTF_8);
        Document document;
        try {
            document = Jsoup.connect(url).get();
            Elements elements = document.select("div[style=padding-left:50px]");
            for (Element element : elements) {
                if (element.text().equals(WORD_ABSENT_TEXT)) {
                    return false;
                }
            }
        } catch (IOException e) {
            throw new IOException("Не могу подключиться к базе русских слов " + url);
        } catch (Selector.SelectorParseException e) {
            throw new IOException("Невалидный CSS запрос в методе парсинга базы русских слов " + url);
        }

        return true;
    }
}
