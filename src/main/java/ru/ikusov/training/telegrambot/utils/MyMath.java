package ru.ikusov.training.telegrambot.utils;

import static java.lang.Math.abs;

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
     * @param range range of random number to return
     * @return random number from 0 to range
     */
    public static int r(int range) {
        return (int) (Math.random()*range);
    }

    /**
     * returns random number from 0 to 100
     * @return random number from 0 to 100
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

    /**
     * convert nanos to readable time depends on nanos quantity
     * from nanos to hours and minutes inclusive
     * @param nanos nanos quantity to convert
     * @return readable time string
     */
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

    public static String secondsToReadableTime(long seconds) {
        if (seconds<60)
            return String.format("%d секунд%s", seconds, Linguistic.getFemaleWordEnding((int)seconds));
        if (seconds<60*60)
            return String.format("%d минут%s %d секунд%s",
                    seconds/60, Linguistic.getFemaleWordEnding((int)seconds/60),
                    seconds%60, Linguistic.getFemaleWordEnding((int)seconds%60));
        else
            return String.format("%d час%s %d минут%s %d секунд%s",
                    seconds/60/60, Linguistic.getMaleWordEnding((int)seconds/60/60),
                    seconds/60%60, Linguistic.getFemaleWordEnding((int)seconds/60%60),
                    seconds%60, Linguistic.getFemaleWordEnding((int)seconds%60));
    }

    /**
     * check if number is ladder (every next digit different from previous by fixed number)
     * @param number
     * @return
     */
    public static int ladderLength(long number) {
        number = Math.abs(number);
        if (number<100) return 0;

        int[] cyphers = getCyphers(number);
        int dif = cyphers[1] - cyphers[0];

        if (dif == 0) return 0;

        for (int i=2; i<cyphers.length; i++) {
            if (cyphers[i] - cyphers[i-1] != dif)
                return 0;
        }

        return cyphers.length;
    }

    /**
     * if number is palindrome, returns its length
     * else returns zero
     * @param number number to check palindrome
     * @return length of number number
     */
    public static int palindromeLength(long number) {
        number = abs(number);
        if (number<100) return 0;

        int[] cyphers = getCyphers(number);
        int i=0, j=cyphers.length-1;

        while (i<j) {
            if (cyphers[i] != cyphers[j])
                return 0;
            i++;
            j--;
        }
        return cyphers.length;
    }

    /**
     * checks if number is one digited
     * @param number number to check
     * @return if number is one digited
     */
    public static boolean isFromOneDigit(long number) {
        number = Math.abs(number);
        if (number<10) return false;

        int[] cyphers = getCyphers(number);
        int cypher0 = cyphers[0];
        for (int cypher : cyphers) {
            if(cypher!=cypher0)
                return false;
        }
        return true;
    }

    /**
     * Return cyphers array of the number
     * @param number number for split to cyphers
     * @return cyphers split by number
     */
    public static int[] getCyphers(long number) {
        int[] cyphers = new int[20];
        int[] cyphersRev;
        int len;

        for (len=0; number !=0 ; len++) {
            cyphers[len] = (int)number%10;
            number /= 10;
        }

        cyphersRev = new int[len];
        for (int i=0; i<len; i++) {
            cyphersRev[i] = cyphers[len-i-1];
        }

        return cyphersRev;
    }
}
