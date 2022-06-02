package ru.ikusov.training.telegrambot.services.wordle;

import junit.framework.TestCase;

import java.io.IOException;
import java.util.Arrays;

import static ru.ikusov.training.telegrambot.services.wordle.WordleUtils.compareWords;
import static ru.ikusov.training.telegrambot.services.wordle.WordleUtils.formatToMarkdownV2;

public class WordleUtilsTest extends TestCase {

    public void testCompareWords_IfOneMatches() {
        String tested = "дрова";
        String right = "спина";
        int[] expected = {0, 0, 0, 0, 2};
        int[] actual = compareWords(tested, right);

        boolean result = Arrays.equals(expected, actual);

        assertTrue(result);
    }

    public void testCompareWords_IfOneMatchesOfTwo() {
        String tested = "парта";
        String right = "мечта";
        int[] expected = {0, 0, 0, 2, 2};
        int[] actual = compareWords(tested, right);

        boolean result = Arrays.equals(expected, actual);

        assertTrue(result);
    }

    public void testCompareWords_IfOnePresentsOfTwo() {
        String tested = "парад";
        String right = "мечта";
        int[] expected = {0, 1, 0, 0, 0};
        int[] actual = compareWords(tested, right);

        System.out.println(Arrays.toString(actual));

        boolean result = Arrays.equals(expected, actual);

        assertTrue(result);
    }

    public void testCompareWords_IfTwoPresents() {
        String tested = "парад";
        String right = "груда";
        int[] expected = {0, 1, 1, 0, 1};
        int[] actual = compareWords(tested, right);

        System.out.println(Arrays.toString(actual));

        boolean result = Arrays.equals(expected, actual);

        assertTrue(result);
    }

    public void testCompareWords_IfTwoMatchesAndOnePresents() {
        String tested = "парад";
        String right = "карта";
        int[] expected = {0, 2, 2, 1, 0};
        int[] actual = compareWords(tested, right);

        System.out.println(Arrays.toString(actual));

        boolean result = Arrays.equals(expected, actual);

        assertTrue(result);
    }

    public void testCompareWords_IfTwoMatchesAndOnePresents2() {
        String tested = "драка";
        String right = "лодка";
        int[] expected = {1, 0, 0, 2, 2};
        int[] actual = compareWords(tested, right);

        System.out.println(Arrays.toString(actual));

        boolean result = Arrays.equals(expected, actual);

        assertTrue(result);
    }

    public void testCompareWords_IfOneMatchesOfFive() {
        String tested = "ааааа";
        String right = "лодка";
        int[] expected = {0, 0, 0, 0, 2};
        int[] actual = compareWords(tested, right);

        System.out.println(Arrays.toString(actual));

        boolean result = Arrays.equals(expected, actual);

        assertTrue(result);
    }

    public void testCompareWords_IfOneMatchesAndTwoPresents() {
        String tested = "акааа";
        String right = "ладка";
        int[] expected = {1, 1, 0, 0, 2};
        int[] actual = compareWords(tested, right);

        System.out.println(Arrays.toString(actual));

        boolean result = Arrays.equals(expected, actual);

        assertTrue(result);
    }

    public void testCompareWords_IfOneMatchesAndThreePresents() {
        String tested = "дкааа";
        String right = "ладка";
        int[] expected = {1, 1, 1, 0, 2};
        int[] actual = compareWords(tested, right);

        System.out.println(Arrays.toString(actual));

        boolean result = Arrays.equals(expected, actual);

        assertTrue(result);
    }

    public void testFormatToMarkdownV2() {
        String tested = "стена";
        String right = "мечта";

        String expected = "~С~_Т_\r_Е_\r~Н~*А*";
        String actual = formatToMarkdownV2(tested, compareWords(tested, right));

        assertEquals(expected, actual);
    }

    public void testToWordleString_replaceOneBigYo() {
        String s = "рУжьЁ";

        String expected = "ружье";
        String actual = WordleUtils.toWordleString(s);

        assertEquals(expected, actual);
    }

    public void testToWordleString_replaceOneSmallYo() {
        String s = "Бельё";

        String expected = "белье";
        String actual = WordleUtils.toWordleString(s);

        assertEquals(expected, actual);
    }

    public void testToWordleString_replaceSomeBigYo() {
        String s = "КЁСЁМ";

        String expected = "кесем";
        String actual = WordleUtils.toWordleString(s);

        assertEquals(expected, actual);
    }

    public void testToWordleString_replaceSomeSmallYo() {
        String s = "БёЛёк";

        String expected = "белек";
        String actual = WordleUtils.toWordleString(s);

        assertEquals(expected, actual);
    }

    public void testIsWordExists_ifExistingWord() {
        boolean actual = false;

        try {
            actual = WordleUtils.isWordExistsOnGramotaRu("словообразование");
        } catch (IOException e) {
            System.out.println("У меня проблэма! Ламба или фэра! " + e.getMessage());
        }

        assertTrue(actual);
    }

    public void testIsWordExists_ifNotExistingWord() {
        boolean actual = true;

        try {
            actual = WordleUtils.isWordExistsOnGramotaRu("апуапвааыпывппыккви");
        } catch (IOException e) {
            System.out.println("У меня проблэма! Ламба или фэра! " + e.getMessage());
        }

        assertFalse(actual);
    }
}