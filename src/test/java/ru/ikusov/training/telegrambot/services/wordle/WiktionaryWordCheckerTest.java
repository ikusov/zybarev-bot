package ru.ikusov.training.telegrambot.services.wordle;

import junit.framework.TestCase;

import java.io.IOException;

public class WiktionaryWordCheckerTest extends TestCase {

    public void testCheck_IfWordExists() {
        WiktionaryWordChecker wc = new WiktionaryWordChecker();

        boolean expected = true;
        boolean actual = true;

        try {
            actual = wc.check("эфедрин");
        } catch (IOException ignore) {}

        assertEquals(expected, actual);
    }

    public void testCheck_IfWordNotExists() {
        WiktionaryWordChecker wc = new WiktionaryWordChecker();

        boolean expected = false;
        boolean actual = false;

        try {
            actual = wc.check("лаоывалдвоаылд");
        } catch (IOException ignore) {}

        assertEquals(expected, actual);
    }
}