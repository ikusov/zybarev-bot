package ru.ikusov.training.telegrambot.services;

import java.util.List;
import java.util.function.BinaryOperator;

public class Example {
    private final static int RANGE = 1000;

    public enum Operator {
        PLUS("+", (a, b) -> a+b),
        MINUS("-", (a, b) -> a-b),
        MULTIPLY("×", (a, b) -> a*b);

        private final String symbol;
        private final BinaryOperator<Integer> operate;

        Operator(String symbol, BinaryOperator<Integer> operate) {
            this.symbol = symbol;
            this.operate = operate;
        }

        public Integer apply(int a, int b) {
            return operate.apply(a, b);
        }

        @Override
        public String toString() {
            return symbol;
        }
    }

    public enum Complexity {
        EASY(RANGE/100),
        MEDIUM(RANGE/10),
        HARD(RANGE);

        private final int range;

        Complexity(int range) {
            this.range = range;
        }
    }

    private List<Integer> operands;
    private List<Operator> operators;
}