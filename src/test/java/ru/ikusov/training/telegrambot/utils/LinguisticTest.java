package ru.ikusov.training.telegrambot.utils;

import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;
import java.util.function.Function;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class LinguisticTest {
    String manul = "манул",
            cutlet = "котлет";

    String cutletTestString = "0 котлет, 1 котлета, 2 котлеты, 3 котлеты, 4 котлеты, 5 котлет, 6 котлет, 7 котлет, " +
            "8 котлет, 9 котлет, 10 котлет, 11 котлет, 12 котлет, 13 котлет, 14 котлет, 15 котлет, " +
            "16 котлет, 17 котлет, 18 котлет, 19 котлет, 20 котлет, 21 котлета, 22 котлеты, 23 котлеты, " +
            "24 котлеты, 25 котлет, 26 котлет, 27 котлет, 28 котлет, 29 котлет, 30 котлет, 31 котлета, " +
            "32 котлеты";
    String manulTestString = "0 манулов, 1 манул, 2 манула, 3 манула, 4 манула, 5 манулов, 6 манулов, " +
            "7 манулов, 8 манулов, 9 манулов, 10 манулов, 11 манулов, 12 манулов, 13 манулов, 14 манулов, " +
            "15 манулов, 16 манулов, 17 манулов, 18 манулов, 19 манулов, 20 манулов, 21 манул, 22 манула, " +
            "23 манула, 24 манула, 25 манулов, 26 манулов, 27 манулов, 28 манулов, 29 манулов, 30 манулов," +
            "31 манул, 32 манула, 33 манула, 34 манула, 35 манулов, 36 манулов, 37 манулов, 38 манулов";


    private void testGetWordEnding(String source, String testString, Function<Integer, String> endingGetter) {
        List<String> testStrings = Arrays.stream(testString.split(","))
                .map(String::trim)
                .toList();

        for (int i = 0; i < testStrings.size(); i++) {
            String actual = String.format("%d %s%s", i, source, endingGetter.apply(i));
            String expected = testStrings.get(i);
            assertEquals(expected, actual);
        }
    }

    @Test
    public void testGetManulWordEnding() {
        testGetWordEnding(manul, manulTestString, Linguistic::getManulWordEnding);
    }

    @Test
    public void testGetCutletWordEnding() {
        testGetWordEnding(cutlet, cutletTestString, Linguistic::getCutletWordEnding);
    }
}