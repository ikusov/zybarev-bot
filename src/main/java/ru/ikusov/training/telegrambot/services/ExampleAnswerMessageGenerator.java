package ru.ikusov.training.telegrambot.services;

import org.telegram.telegrambots.meta.api.objects.User;
import ru.ikusov.training.telegrambot.model.UserEntity;

public class ExampleAnswerMessageGenerator {
    private MathAchieves achieves;

    public String generate(UserEntity user) {
        String userName = UserNameGetter.getUserName(user);

        StringBuilder msgB = new StringBuilder();

        msgB.append(String.format("Победитель - %s. Так держать! ", userName));
        msgB.append(achieves.getTimeMessage(userName));

        return msgB.toString();
    }
}
