package ru.ikusov.training.telegrambot.services;

import org.telegram.telegrambots.meta.api.objects.User;
import ru.ikusov.training.telegrambot.model.ChatEntity;
import ru.ikusov.training.telegrambot.model.UserEntity;
import ru.ikusov.training.telegrambot.utils.Linguistic;

public class ExampleAnswerMessageGenerator {
    private MathAchieves achieves;
    private UserEntity user;
    private final int userAnswer;
    private final int rightAnswer;
    private final boolean isRight;
    private int sum;

    public ExampleAnswerMessageGenerator(DatabaseConnector databaseConnector, UserEntity user,
                                         ChatEntity chat, int userAnswer, int rightAnswer, int timer) {
        this.userAnswer = userAnswer;
        this.rightAnswer = rightAnswer;
        this.isRight = userAnswer==rightAnswer;
        if (isRight) {
            this.user = user;
            achieves = new MathAchieves(databaseConnector, user, chat, rightAnswer, timer);
            sum = achieves.getSum();
        }
    }

    public String generate() {
        return isRight ?
                generateRightAnswerMessage() :
                generateWrongAnswerMessage();
    }

    private String generateWrongAnswerMessage() {
        int dif = Math.abs(userAnswer-rightAnswer);
        if (dif<6) return "So close...";
        if (dif==10) return "Десяточка обиженно смотрит на тебя!";
        if (dif==100) return "Соточка обиженно смотрит на тебя!";
        if (dif==200) return "Двухсоточка обиженно смотрит на тебя!";
        else return "Неправильно! Попробуй ещё раз.";
    }

    private String generateRightAnswerMessage() {
        String userName = UserNameGetter.getUserName(user);
        int sum = achieves.getSum();

        StringBuilder msgB = new StringBuilder();

        msgB.append(String.format("Победитель - %s. Так держать! ", userName));
        msgB.append(achieves.getTimeMessage(userName));
        achieves.getAchieveList().forEach(a -> msgB.append(String.format("+ %d %s%n", a.getBonus(), a.getMessage())));
        msgB.append(String.format("Итого: %d мат. балл%s", sum, Linguistic.getMaleWordEnding(sum)));

        return msgB.toString();
    }

    public int getSum() {
        return sum;
    }
}
