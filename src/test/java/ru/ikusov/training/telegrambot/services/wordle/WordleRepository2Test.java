package ru.ikusov.training.telegrambot.services.wordle;

import junit.framework.TestCase;

public class WordleRepository2Test extends TestCase {
    private final long USER_ID = 42;
    private final long CHAT_ID = 34;
    WordleRepository2 wr = new WordleRepository2(null);

    public void testGetOrCreateWordAttempt() {
        wr.getOrCreateWordAttempt(USER_ID, CHAT_ID, 5);
    }

    public void testSaveWordAttempt() {
        wr.saveWordAttempt("kjlkj", USER_ID, CHAT_ID);
    }

    public void testGetShuffledIndexesArray() {
        int expected = 915;

        var array = wr.getShuffledIndexesArray(-15L, expected);
        for (var elem : array) {
            System.out.println(elem);
        }
        int actual = array.length;

        assertEquals(expected, actual);
    }

    public void testGetNextRandomWordForChat() {
        long chatId_1 = CHAT_ID;

        var word1 = "";
        try {
            word1 = wr.getNextRandomWordForChat(chatId_1);
        } catch (Exception e) {
            System.err.println(e.getMessage());
        }

        System.out.println(word1);
    }

    public void testGetCurrentWordForChat() {
        var word = wr.getCurrentWordForChat(CHAT_ID);

        assertEquals(word, "овал");
    }

    public void testGetTriedWordsForChat() {
        var words = wr.getTriedWordsForChat(CHAT_ID);
        assertNotNull(words);
    }
}