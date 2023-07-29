package ru.ikusov.training.telegrambot.services;

import org.telegram.telegrambots.meta.api.objects.Chat;
import org.telegram.telegrambots.meta.api.objects.User;
import ru.ikusov.training.telegrambot.dao.DatabaseConnector;
import ru.ikusov.training.telegrambot.model.ChatEntity;
import ru.ikusov.training.telegrambot.model.UserEntity;
import ru.ikusov.training.telegrambot.utils.Linguistic;
import ru.ikusov.training.telegrambot.utils.MessageType;
import ru.ikusov.training.telegrambot.utils.MyMath;

import static ru.ikusov.training.telegrambot.MainClass.AVTFTALK_CHAT_ID;

public class ExampleAnswerMessageGenerator {
    private MathAchieves achieves;
    private final String userName;
    private final int userAnswer;
    private final int rightAnswer;
    private final boolean isRight;
    private final boolean isAvtftalk;
    private final int timer;
    private int exampleScore;
    private int sumScore;

    public ExampleAnswerMessageGenerator(DatabaseConnector databaseConnector, UserEntity user,
                                         ChatEntity chat, int userAnswer, int rightAnswer, int timer) {
        this.isAvtftalk = String.valueOf(chat.getId()).equals(AVTFTALK_CHAT_ID);
        this.userAnswer = userAnswer;
        this.rightAnswer = rightAnswer;
        this.isRight = userAnswer==rightAnswer;
        this.timer = timer;
        this.userName = UserNameGetter.getUserName(user);
        if (isRight && isAvtftalk) {
            achieves = new MathAchieves(databaseConnector, user, chat, rightAnswer, timer);
            exampleScore = achieves.getSum();
            sumScore = achieves.getScore();
        }
    }

    public ExampleAnswerMessageGenerator(MathAchieves achieves, User user,
                                         Chat chat, int userAnswer, int rightAnswer, int timer) {
        this.isAvtftalk = String.valueOf(chat.getId()).equals(AVTFTALK_CHAT_ID);
        this.userAnswer = userAnswer;
        this.rightAnswer = rightAnswer;
        this.isRight = userAnswer==rightAnswer;
        this.timer = timer;
        this.userName = UserNameGetter.getUserName(user);
        if (isRight && isAvtftalk) {
            this.achieves = achieves;
            exampleScore = achieves.getSum();
            sumScore = achieves.getScore();
        }
    }

    public String generate() {
        return isRight ?
                isAvtftalk ?
                generateRightAnswerMessage() :
                generateSimpleRightAnswerMessage() :
                generateWrongAnswerMessage();
    }

    private String generateWrongAnswerMessage() {
        int dif = Math.abs(userAnswer - rightAnswer);
        if (dif<6) return "So close...";
        if (dif==10) return "Десяточка обиженно смотрит на тебя!";
        if (dif==100) return "Соточка обиженно смотрит на тебя!";
        if (dif==200) return "Двухсоточка обиженно смотрит на тебя!";
        else return "Неправильно! Попробуй ещё раз.";
    }

    private String generateSimpleRightAnswerMessage() {
        return String.format(
                MessageType.RIGHT_ANSWER_MESSAGE.getRandomMessage(),
                userAnswer,
                userName,
                MyMath.secondsToReadableTime(timer));
    }

    private String generateRightAnswerMessage() {
        StringBuilder msgB = new StringBuilder();

        msgB.append(String.format("Победитель - %s. Так держать! ", userName));
        msgB.append(achieves.getTimeMessage(userName));
        achieves.getAchieveList().forEach(a -> msgB.append(String.format("+ %d %s%n", a.getBonus(), a.getMessage())));
        msgB.append(String.format("Итого: %d мат. балл%s ", exampleScore, Linguistic.getManulWordEnding(exampleScore)));
        msgB.append(String.format("(общая сумма %d мат. балл%s)", sumScore+exampleScore, Linguistic.getManulWordEnding(sumScore+exampleScore)));

        return msgB.toString();
    }

    public int getExampleScore() {
        return exampleScore;
    }
}
