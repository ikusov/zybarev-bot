package ru.ikusov.training.telegrambot.services;

import org.springframework.stereotype.Component;
import ru.ikusov.training.telegrambot.utils.Linguistic;
import ru.ikusov.training.telegrambot.utils.MyString;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static ru.ikusov.training.telegrambot.utils.MyMath.r;

@Component
public class CaseQuestionGenerator {
    private final String CASE_QUESTION = "На какие вопросы отвечает существительное в %s падеже?";
    private final String QUESTION_QUESTION = "Существительное в каком падеже отвечает на вопросы \"%s\"?";

    private String question = "";
    private Case answerCase;
    private List<String> answer;
    private boolean isAnswered = true;
    private boolean isCase;

    public String getQuestion() {


        if (!isAnswered) return question;

        answerCase = Case.values()[r(Case.values().length)];
        isCase = r(10) < 5;
        if (!isCase) {
            question = String.format(CASE_QUESTION,
                    answerCase.getName() +
                    Linguistic.getSevereWordEnding(5));
        } else {
            question = String.format(QUESTION_QUESTION,
                    answerCase.getSpiritedQuestion() + ", " +
                            answerCase.getUnspiritedQuestion()
                    );
        }

        answer = List.of(
                MyString.brutalProcessing(
                        answerCase.getSpiritedQuestion()+answerCase.getUnspiritedQuestion()),
                MyString.brutalProcessing(
                        answerCase.getUnspiritedQuestion()+answerCase.getSpiritedQuestion())
        );
        isAnswered = false;
        return question;
    }

    public boolean isAnswered() {
        return isAnswered;
    }

    public void setAnswered() {
        isAnswered = true;
    }

    /**
     * Testing user answer string of being wrong or right
     * @param userAnswer user answer string to be tested
     * @return less than -1 if userAnswer contains more than required variants
     * -1 if user answer is wrong
     * 0 if user answer contains no variants,
     * 2 if user answer is right
     */
    public int testUserAnswer(String userAnswer) {
        String ua = MyString.brutalProcessing(userAnswer);
        int count = 0;
        var answerVariants = getAnswerVariants();
        for (var var : answerVariants) {
            if (ua.contains(var)) count++;
        }

        if(count==0)
            return 0;

        if (isCase && count==1 && ua.contains(answerCase.getName())) {
            setAnswered();
            return 2;
        }

        if (!isCase && count==1
                && (ua.contains(answer.get(0))
                || ua.contains(answer.get(1)))
        ) {
            setAnswered();
            return 2;
        }

//        if (!isCase && count==1
//                && (ua.contains(answerCase.getSpiritedQuestion())
//                    || ua.contains(answerCase.getUnspiritedQuestion()))
//        )
//            return 1;
//
//        if (!isCase && count==2)
//            return -1;

        return -count;
    }

    public Set<String> getAnswerVariants() {
        return isCase ? getCases() : getQuestions();
    }

    private Set<String> getCases() {
        return Stream.of(Case.values()).map(Case::getName).collect(Collectors.toSet());
    }

    private Set<String> getQuestions() {
        return Stream.of(Case.values())
                .flatMap(value->Stream.of(
                        MyString.brutalProcessing(
                        value.getSpiritedQuestion() + value.getUnspiritedQuestion()),
                        MyString.brutalProcessing(
                        value.getUnspiritedQuestion() + value.getSpiritedQuestion())
                )).collect(Collectors.toSet());
    }

    private enum Case {
        I("именительн", "кто", "что")
        , R("родительн", "кого", "чего")
        , D("дательн", "кому", "чему")
        , V("винительн", "кого", "что")
        , T("творительн", "кем", "чем")
        , P("предложн", "о ком", "о чем")
        ;
        private final String name;
        private final String spiritedQuestion;
        private final String unspiritedQuestion;
        Case(String name, String spiritedQuestion, String unspiritedQuestion) {
            this.name = name;
            this.spiritedQuestion = spiritedQuestion;
            this.unspiritedQuestion = unspiritedQuestion;
        }

        public String getName() {
            return name;
        }

        public String getSpiritedQuestion() {
            return spiritedQuestion;
        }

        public String getUnspiritedQuestion() {
            return unspiritedQuestion;
        }
    }
}
