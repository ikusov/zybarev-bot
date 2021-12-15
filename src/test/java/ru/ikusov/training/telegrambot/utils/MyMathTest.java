package ru.ikusov.training.telegrambot.utils;

import junit.framework.TestCase;
import org.junit.Assert;

import java.util.Iterator;
import java.util.List;

public class MyMathTest extends TestCase {

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

    public void testGetCyphers() {
        long num = 2367;
        int[] cyphersExpected = {2, 3, 6, 7};
        int[] cyphersActual = MyMath.getCyphers(num);

        Assert.assertEquals(cyphersExpected.length, cyphersActual.length);
        for (int i=0; i<cyphersActual.length; i++)
            Assert.assertEquals(cyphersExpected[i], cyphersActual[i]);
    }

    public void testPalindromeLength() {
        List<Integer> nums =                         List.of(1, 22, 232, 325, 4444, 5632365, 720, 0, -565);
        List<Integer> palindromeLengthExpected =    List.of(0, 0,  3  , 0  , 4   , 7      , 0  , 0,  3);

        Iterator<Integer> numIterator = nums.listIterator();
        Iterator<Integer> palindromeLengthExpectedIterator = palindromeLengthExpected.listIterator();

        while(numIterator.hasNext()) {
            long num = numIterator.next();
            long palLengthExpected = palindromeLengthExpectedIterator.next();
            Assert.assertEquals(palLengthExpected, MyMath.palindromeLength(num));
        }
    }

    public void testIsFromOneDigit() {
        var nums =    List.of(1,      22,     232,    -325,   4444,   -5555555,   7,      111,    -665);
        var exps =     List.of(false,  true,   false,  false,  true,   true,       false,  true,   false);

        var numsi = nums.listIterator();
        var expsi = exps.listIterator();

        while(numsi.hasNext()) {
            long num = numsi.next();
            boolean exp = expsi.next();
            Assert.assertEquals(exp, MyMath.isFromOneDigit(num));
        }
    }
}