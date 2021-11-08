package ru.ikusov.training.telegrambot.services;

import org.springframework.stereotype.Component;

import static ru.ikusov.training.telegrambot.utils.MyMath.r;

@Component
public class ExampleGenerator {
    private static final int RANGE = 1000;
    private final String[] operators = new String[]{"×", "+", "-"};

    private String example,
                    answer = String.valueOf(r(RANGE));
    private int answerInt;
    private long timer;

    private boolean answered = true;

    public ExampleGenerator() {}

    public String generateExample(Complexity complexity) {
        String operator = operators[r(operators.length)];
        int operand1, operand2;

        if (complexity == Complexity.EASY) {
            operator = "×";
            operand1 = 3+r(8);
            operand2 = 3+r(8);
        } else {
            operand1 = r(complexity.range);
            operand2 = r(complexity.range);
        }

        answerInt = operator.equals("+") ? operand1 + operand2 :
                    operator.equals("-") ? operand1-operand2 :
                    operand1*operand2;
        answer = String.valueOf(answerInt);
        example = String.format("%s%s%s", operand1, operator, operand2);
        setAnswered(false);
        timer = System.nanoTime();

        return example;
    }

    public String getAnswer() {
        return answer;
    }

    public int getAnswerInt() {return answerInt;}

    public String getExample() {
        return example;
    }

    public long getTimer() { return timer; }

    public boolean isAnswered() {
        return answered;
    }

    public void setAnswered(boolean answered) {
        this.answered = answered;
    }


    public enum Complexity {
        EASY(RANGE/100),
        MEDIUM(RANGE/10),
        HARD(RANGE);

        private int range;

        Complexity (int range) {
            this.range = range;
        }

        public static Complexity getRandomComplexity() {
            int random = r(3);
            return random == 0 ? Complexity.EASY :
                    random == 1 ? Complexity.MEDIUM :
                            Complexity.HARD;
        }
    }
}
