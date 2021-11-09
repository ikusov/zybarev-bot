package ru.ikusov.training.telegrambot.utils;

import org.junit.Assert;
import org.junit.Test;

public class MyStringTest {
//    @Test
//    public void brutalParseIntTest() {
//        String string = "---";
//        int expected = -125;
//        int actual = MyString.brutalParseInt(string);
//
//        Assert.assertEquals(expected, actual);
//    }
//
    @Test
    public void trimPunctuationMarksInclusiveTest() {
        String string = "           Хороший пример!...    !!!111     .....";
        String expected = "Хороший пример";
        String actual = MyString.trimPunctuationMarksInclusive(string);

        Assert.assertEquals(expected, actual);
    }
}