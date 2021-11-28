package ru.ikusov.training.telegrambot.services;

import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import static ru.ikusov.training.telegrambot.utils.MyMath.r;

@Component
public class ExampleGenerator {
    private static final int RANGE = 100;
    private final String[] operators = new String[]{"×", "+", "-"};

    private String example = "",
                    answer = String.valueOf(r(RANGE));
    private int answerInt;
    private long timer;

    private boolean answered = true;

    public ExampleGenerator() {}

    public String generateExampleNew(Complexity complexity) {
        List<String> operatorList = new LinkedList<>();
        List<Integer> operandList = new LinkedList<>();
        int operandLength = 3+r(3);

        for (int i=0; i<operandLength; i++) {
            operandList.add(r(complexity.range));
            if (i<operandLength-1) {
                operatorList.add(
                        complexity==Complexity.EASY &&
                        operatorList.contains("×") ?
                                operators[1+r(operators.length-1)] :
                                operators[r(operators.length)]
                );
            }
        }

        //form example string from operand and operator lists
        example = "" + operandList.get(0);
        for (int i=1; i<operandLength; i++) {
            String operator = operatorList.get(i-1);
            int operand = operandList.get(i);
            example += operator + operand;
        }

        //calculate the example
        for (int i=0; i<operandLength-1; i++) {
            if(operatorList.get(i).equals("×")) {
                int operand1 = operandList.get(i),
                        operand2 = operandList.get(i+1);
                operandList.set(i, operand1*operand2);
                operandList.remove(i+1);
                operatorList.remove(i);
                i--;
                operandLength--;
            }
        }

        int total = operandList.get(0);
        for (int i=1; i<operandLength; i++) {
            total = operatorList.get(i-1).equals("+") ?
                    total + operandList.get(i) :
                    total - operandList.get(i);
        }

        answerInt = total;
        answer = String.valueOf(answerInt);

        setAnswered(false);
        timer = System.nanoTime();

        return example;
    }

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
        EASY(RANGE/10),
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
