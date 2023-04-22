package ru.ikusov.training.telegrambot.utils;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class CommonUtilsTest {

    @Test
    void countTrues() {
        int expected = 4;
        int actual = CommonUtils.countTrues(new boolean[]{true, false, true, false, true, true});

        assertEquals(expected, actual);
    }
}