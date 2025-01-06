package ru.ikusov.training.telegrambot;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class TestUtils {
    /**
     * Delaying current thread for random time.
     *
     * @param minDelayTime minimal delay time in millis
     * @param maxDelayTime maximal delay time in millis
     */
    public static void delayRandomTime(long minDelayTime, long maxDelayTime) {
        long time = (long) (minDelayTime + Math.random() * (maxDelayTime - minDelayTime));

        try {
            Thread.sleep(time);
        } catch (InterruptedException ignored) {
        }
    }
}
