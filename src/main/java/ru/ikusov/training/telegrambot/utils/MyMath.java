package ru.ikusov.training.telegrambot.utils;

/**
 * class for own "math" utilities
 */
public class MyMath {

    /**
     * check num for being prime
     * @param num - checked number
     * @return true if num is prime
     */
    public static boolean isPrime(long num) {
        boolean isPrime = true;
        for (long i=2; i<num; i++) {
            if (num%i == 0) {
                isPrime = false;
                break;
            }
        }
        return isPrime;
    }

    /**
     * returns random number from 0 to range
     * @param range
     * @return
     */
    public static int r(int range) {
        return (int) (Math.random()*range);
    }

    /**
     * returns random number from 0 to 100
     * @return
     */
    public static int r() {
        return r(100);
    }

    /**
     * returns random n-character string
     * @param n number of characters
     * @return n-character brand new random streeng
     */
    public static String rs(int n) {
        char[] s = new char[n];
        for (int i=0; i<n; i++) {
            s[i] = (char)(Math.random()*('z' - 'a')+'a');
        }

        return String.valueOf(s);
    }

    /**
     * returns random 10-character string
     * @return random 10-character string
     */
    public static String rs() {
        return rs(10);
    }

    /**
     * returns brand new random array of int numbers
     * @param size the size of returned brand new array of int numbers
     * @return brand new random array of int numbers
     */
    public static int[] getRandomArray(int size) {
        int[] randomArray = new int[size];
        for (int i=0; i<size; randomArray[i++] = r(size));
        return randomArray;
    }

    public static String toReadableTime(long nanos) {
        String readableTime;
        if (nanos < 1000)
            return String.format("%d nanos", nanos);
        if (nanos < 1000_000)
            return String.format("%d.%d us", nanos/1000, nanos%1000/10);
        if (nanos < 1000_000_000)
            return String.format("%d.%d ms", nanos/1000_000, nanos%1000_000/10_000);
        if (nanos < 60_000_000_000L)
            return String.format("%d.%d s", nanos/1_000_000_000L, nanos%1_000_000_000L/10_000_000);
        if (nanos < 3600_000_000_000L)
            return String.format("%dm %ds", nanos/60_000_000_000L, nanos%60_000_000_000L/1_000_000_000);
        else
            return String.format("%dh %dm", nanos/3_600_000_000_000L, nanos%3_600_000_000_000L/60_000_000_000L);
    }
}
