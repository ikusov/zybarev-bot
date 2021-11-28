package ru.ikusov.training.telegrambot.services;

import junit.framework.TestCase;

public class ExampleGeneratorTest extends TestCase {

    public void testGenerateExampleNew() {
        ExampleGenerator eg = new ExampleGenerator();
        String example = eg.generateExampleNew(ExampleGenerator.Complexity.getRandomComplexity());
        String answer = eg.getAnswer();

        System.out.printf("Example: %s, answer: %s", example, answer);
    }
}