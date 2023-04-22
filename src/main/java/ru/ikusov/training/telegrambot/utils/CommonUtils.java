package ru.ikusov.training.telegrambot.utils;

import java.util.Collection;

public final class CommonUtils {
    private CommonUtils() {}

    public static boolean isNullOrEmpty(Collection<?> collection) {
        return collection == null || collection.isEmpty();
    }

    public static int countTrues(boolean[] booleans) {
        int count = 0;
        for (boolean b : booleans) {
            if (b) {
                count++;
            }
        }
        return count;
    }
}
