package ru.ikusov.training.telegrambot.services.wordle;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.io.IOException;
import java.util.Arrays;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.params.provider.Arguments.arguments;
import static ru.ikusov.training.telegrambot.services.wordle.WordleUtils.compareWords;
import static ru.ikusov.training.telegrambot.services.wordle.WordleUtils.formatToMarkdownV2;

public class WordleUtilsTest {

    @Test
    public void testCompareWords_IfOneMatches() {
        String tested = "дрова";
        String right = "спина";
        int[] expected = {0, 0, 0, 0, 2};
        int[] actual = compareWords(tested, right);

        boolean result = Arrays.equals(expected, actual);

        assertTrue(result);
    }

    @Test
    public void testCompareWords_IfOneMatchesOfTwo() {
        String tested = "парта";
        String right = "мечта";
        int[] expected = {0, 0, 0, 2, 2};
        int[] actual = compareWords(tested, right);

        boolean result = Arrays.equals(expected, actual);

        assertTrue(result);
    }

    @Test
    public void testCompareWords_IfOnePresentsOfTwo() {
        String tested = "парад";
        String right = "мечта";
        int[] expected = {0, 1, 0, 0, 0};
        int[] actual = compareWords(tested, right);

        System.out.println(Arrays.toString(actual));

        boolean result = Arrays.equals(expected, actual);

        assertTrue(result);
    }

    @Test
    public void testCompareWords_IfTwoPresents() {
        String tested = "парад";
        String right = "груда";
        int[] expected = {0, 1, 1, 0, 1};
        int[] actual = compareWords(tested, right);

        System.out.println(Arrays.toString(actual));

        boolean result = Arrays.equals(expected, actual);

        assertTrue(result);
    }

    @Test
    public void testCompareWords_IfTwoMatchesAndOnePresents() {
        String tested = "парад";
        String right = "карта";
        int[] expected = {0, 2, 2, 1, 0};
        int[] actual = compareWords(tested, right);

        System.out.println(Arrays.toString(actual));

        boolean result = Arrays.equals(expected, actual);

        assertTrue(result);
    }

    @Test
    public void testCompareWords_IfTwoMatchesAndOnePresents2() {
        String tested = "драка";
        String right = "лодка";
        int[] expected = {1, 0, 0, 2, 2};
        int[] actual = compareWords(tested, right);

        System.out.println(Arrays.toString(actual));

        boolean result = Arrays.equals(expected, actual);

        assertTrue(result);
    }

    @Test
    public void testCompareWords_IfOneMatchesOfFive() {
        String tested = "ааааа";
        String right = "лодка";
        int[] expected = {0, 0, 0, 0, 2};
        int[] actual = compareWords(tested, right);

        System.out.println(Arrays.toString(actual));

        boolean result = Arrays.equals(expected, actual);

        assertTrue(result);
    }

    @Test
    public void testCompareWords_IfOneMatchesAndTwoPresents() {
        String tested = "акааа";
        String right = "ладка";
        int[] expected = {1, 1, 0, 0, 2};
        int[] actual = compareWords(tested, right);

        System.out.println(Arrays.toString(actual));

        boolean result = Arrays.equals(expected, actual);

        assertTrue(result);
    }

    @Test
    public void testCompareWords_IfOneMatchesAndThreePresents() {
        String tested = "дкааа";
        String right = "ладка";
        int[] expected = {1, 1, 1, 0, 2};
        int[] actual = compareWords(tested, right);

        System.out.println(Arrays.toString(actual));

        boolean result = Arrays.equals(expected, actual);

        assertTrue(result);
    }

    @Test
    public void testFormatToMarkdownV2() {
        String tested = "стена";
        String right = "мечта";

        String expected = "~С~_Т_\r_Е_\r~Н~*А*";
        String actual = formatToMarkdownV2(tested, compareWords(tested, right));

        assertEquals(expected, actual);
    }

    @Test
    public void testToWordleString_replaceOneBigYo() {
        String s = "рУжьЁ";

        String expected = "ружье";
        String actual = WordleUtils.toWordleString(s);

        assertEquals(expected, actual);
    }

    @Test
    public void testToWordleString_replaceOneSmallYo() {
        String s = "Бельё";

        String expected = "белье";
        String actual = WordleUtils.toWordleString(s);

        assertEquals(expected, actual);
    }

    @Test
    public void testToWordleString_replaceSomeBigYo() {
        String s = "КЁСЁМ";

        String expected = "кесем";
        String actual = WordleUtils.toWordleString(s);

        assertEquals(expected, actual);
    }

    @Test
    public void testToWordleString_replaceSomeSmallYo() {
        String s = "БёЛёк";

        String expected = "белек";
        String actual = WordleUtils.toWordleString(s);

        assertEquals(expected, actual);
    }

    @Test
    public void testIsWordExists_ifExistingWord() {
        boolean actual = false;

        try {
            actual = WordleUtils.isWordExistsOnGramotaRu("словообразование");
        } catch (IOException e) {
            System.out.println("У меня проблэма! Ламба или фэра! " + e.getMessage());
        }

        assertTrue(actual);
    }

    @Test
    public void testIsWordExists_ifNotExistingWord() {
        boolean actual = true;

        try {
            actual = WordleUtils.isWordExistsOnGramotaRu("апуапвааыпывппыккви");
        } catch (IOException e) {
            System.out.println("У меня проблэма! Ламба или фэра! " + e.getMessage());
        }

        assertFalse(actual);
    }

    //<editor-fold desc="isAlmostFullOfTwos">
    @ParameterizedTest
    @MethodSource
    void isAlmostFullOfTwos(int[] numbers, boolean expectedResult) {
        boolean actualResult = WordleUtils.isAlmostFullOfTwos(numbers);

        assertEquals(expectedResult, actualResult);
    }


    public static Stream<Arguments> isAlmostFullOfTwos() {
        return Stream.of(
                arguments(new int[]{2, 2, 2}, false),
                arguments(new int[]{2, 2, 1}, true),
                arguments(new int[]{2, 2, 1, 1}, true),
                arguments(new int[]{0, 2, 0, 0, 2}, false),
                arguments(new int[]{2, 2, 2, 0, 2}, true),
                arguments(new int[]{0, 2, 2, 2, 2}, true),
                arguments(new int[]{1, 2, 2, 2, 2}, true),
                arguments(new int[]{1, 2, 2, 2, 1}, true),
                arguments(new int[]{1, 2, 2, 1, 1}, false),
                arguments(new int[]{1, 2, 2, 0, 1}, false),
                arguments(new int[]{1, 2, 2, 2, 1}, true),
                arguments(new int[]{1, 1, 2, 0, 1}, false),
                arguments(new int[]{}, false)
        );
    }
    //</editor-fold>
}