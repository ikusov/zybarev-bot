package ru.ikusov.training.telegrambot.utils;

import org.junit.jupiter.api.Test;

import java.util.Iterator;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class MyMathTest {

    @Test
    public void testLadderLength() {
        List<Integer> numbersLadder3 = List.of(123, 321, 468, 963);
        List<Integer> numbersLadder4 = List.of(1234, 3210, 2468, 9630);
        List<Integer> numbersLadder5 = List.of(12345, 43210, 13579, 98765);
        List<Integer> numbersNotLadder = List.of(23, 130, 4444, 161, 260);

        numbersLadder3.forEach(num -> assertEquals(3, MyMath.ladderLength(num)));
        numbersLadder4.forEach(num -> assertEquals(4, MyMath.ladderLength(num)));
        numbersLadder5.forEach(num -> assertEquals(5, MyMath.ladderLength(num)));
        numbersNotLadder.forEach(num -> assertEquals(0, MyMath.ladderLength(num)));
    }

    @Test
    public void testGetCyphers() {
        long num = 2367;
        int[] cyphersExpected = {2, 3, 6, 7};
        int[] cyphersActual = MyMath.getCyphers(num);

        assertEquals(cyphersExpected.length, cyphersActual.length);
        for (int i = 0; i < cyphersActual.length; i++)
            assertEquals(cyphersExpected[i], cyphersActual[i]);
    }

    @Test
    public void testPalindromeLength() {
        List<Integer> nums = List.of(1, 22, 232, 325, 4444, 5632365, 720, 0, -565);
        List<Integer> palindromeLengthExpected = List.of(0, 0, 3, 0, 4, 7, 0, 0, 3);

        Iterator<Integer> numIterator = nums.listIterator();
        Iterator<Integer> palindromeLengthExpectedIterator = palindromeLengthExpected.listIterator();

        while (numIterator.hasNext()) {
            long num = numIterator.next();
            long palLengthExpected = palindromeLengthExpectedIterator.next();
            assertEquals(palLengthExpected, MyMath.palindromeLength(num));
        }
    }

    @Test
    public void testIsFromOneDigit() {
        var nums = List.of(1, 22, 232, -325, 4444, -5555555, 7, 111, -665);
        var exps = List.of(false, true, false, false, true, true, false, true, false);

        var numsi = nums.listIterator();
        var expsi = exps.listIterator();

        while (numsi.hasNext()) {
            long num = numsi.next();
            boolean exp = expsi.next();
            assertEquals(exp, MyMath.isFromOneDigit(num));
        }
    }

    @Test
    public void testSecondsToReadableTime_IfAllOnes() {
        int hours = 1;
        int minutes = 1;
        int seconds = 1;
        int timeIntervalInSeconds = hours * 3600 + minutes * 60 + seconds;

        String expected = String.format("%d час %d минута %d секунда", hours, minutes, seconds);
        String actual = MyMath.secondsToReadableTime(timeIntervalInSeconds);

        assertEquals(expected, actual);
    }

    @Test
    public void testSecondsToReadableTime_IfTwos() {
        int hours = 2;
        int minutes = 2;
        int seconds = 2;
        int timeIntervalInSeconds = hours * 3600 + minutes * 60 + seconds;

        String expected = String.format("%d часа %d минуты %d секунды", hours, minutes, seconds);
        String actual = MyMath.secondsToReadableTime(timeIntervalInSeconds);

        assertEquals(expected, actual);
    }

    @Test
    public void testSecondsToReadableTimeVin_IfAllOnes() {
        int hours = 1;
        int minutes = 1;
        int seconds = 1;
        int timeIntervalInSeconds = hours * 3600 + minutes * 60 + seconds;

        String expected = String.format("%d час %d минуту %d секунду", hours, minutes, seconds);
        String actual = MyMath.secondsToReadableTimeVin(timeIntervalInSeconds);

        assertEquals(expected, actual);
    }

    @Test
    public void testSecondsToReadableTimeVin_IfTwos() {
        int hours = 2;
        int minutes = 2;
        int seconds = 2;
        int timeIntervalInSeconds = hours * 3600 + minutes * 60 + seconds;

        String expected = String.format("%d часа %d минуты %d секунды", hours, minutes, seconds);
        String actual = MyMath.secondsToReadableTimeVin(timeIntervalInSeconds);

        assertEquals(expected, actual);
    }

    @Test
    public void testSecondsToReadableTimeVin_IfFives() {
        int hours = 5;
        int minutes = 5;
        int seconds = 5;
        int timeIntervalInSeconds = hours * 3600 + minutes * 60 + seconds;

        String expected = String.format("%d часов %d минут %d секунд", hours, minutes, seconds);
        String actual = MyMath.secondsToReadableTimeVin(timeIntervalInSeconds);

        assertEquals(expected, actual);
    }

    @Test
    public void testSecondsToReadableTimeVin_IfTwentyOnes() {
        int hours = 21;
        int minutes = 21;
        int seconds = 21;
        int timeIntervalInSeconds = hours * 3600 + minutes * 60 + seconds;

        String expected = String.format("%d час %d минуту %d секунду", hours, minutes, seconds);
        String actual = MyMath.secondsToReadableTimeVin(timeIntervalInSeconds);

        assertEquals(expected, actual);
    }

    @Test
    public void testIsPrime_FalseIfLessThan2() {
        long[] lessThan2s = {-13L, -10L, -3L, -1L, 0, 1L};

        for (var num : lessThan2s) {
            var actual = MyMath.isPrime(num);
            assertFalse(actual);
        }
    }

    @Test
    public void testIsPrime_TrueForSomeKnownPrimes() {
        long[] primes = {7, 19, 37, 61, 127, 271, 331, 397, 547, 631, 919, 1657, 1801, 1951,
                2269, 2437, 2791, 3169, 3571, 4219, 4447, 5167, 5419, 6211, 7057, 7351, 8269,
                9241, 10267, 11719, 12097, 13267, 13669, 16651, 19441, 19927, 22447, 23497,
                24571, 25117, 26227, 27361, 33391, 35317,
                13, 109, 193, 433, 769, 1201, 1453, 2029, 3469, 3889, 4801, 10093, 12289,
                13873, 18253, 20173, 21169, 22189, 28813, 37633, 43201, 47629, 60493, 63949,
                65713, 69313, 73009, 76801, 84673, 106033, 108301, 112909, 115249,
                2, 3, 5, 7, 11, 13, 17, 19, 23, 29, 31, 37, 41, 43, 47, 53, 59, 61, 67, 71, 73,
                79, 83, 89, 97, 101, 103, 107, 109, 113, 127, 131, 137, 139, 149, 151, 157, 163,
                167, 173, 179, 181, 191, 193, 197, 199, 211, 223, 227, 229, 233, 239, 241, 251, 257,
                263, 269, 271};

        for (var num : primes) {
            var actual = MyMath.isPrime(num);
            assertTrue(actual);
        }
    }

    @Test
    public void testIsPrime_FalseForSomeNonPrimes() {
        long[] nonPrimes = {4, 6, 8, 9, 10, 12, 14, 15, 16, 18, 20, 21, 22, 24, 26, 65536};

        for (var num : nonPrimes) {
            var actual = MyMath.isPrime(num);
            System.out.println(num + " is prime: " + actual);
            assertFalse(actual);
        }
    }
}