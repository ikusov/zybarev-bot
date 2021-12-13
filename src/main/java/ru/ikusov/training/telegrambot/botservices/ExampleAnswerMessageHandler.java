package ru.ikusov.training.telegrambot.botservices;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Message;
import ru.ikusov.training.telegrambot.model.ChatEntity;
import ru.ikusov.training.telegrambot.model.ExampleAnswerEntity;
import ru.ikusov.training.telegrambot.model.UserEntity;
import ru.ikusov.training.telegrambot.services.DatabaseConnector;
import ru.ikusov.training.telegrambot.services.ExampleGenerator;
import ru.ikusov.training.telegrambot.services.UserNameGetter;
import ru.ikusov.training.telegrambot.utils.MessageType;
import ru.ikusov.training.telegrambot.utils.MyMath;
import ru.ikusov.training.telegrambot.utils.MyString;
import ru.ikusov.training.telegrambot.utils.RandomMessageGenerator;

@Component
@Order(10)
public class ExampleAnswerMessageHandler extends NonCommandMessageHandler {
    @Autowired
    private ExampleGenerator exampleGenerator;

    @Autowired
    DatabaseConnector databaseConnector;

    @Override
    public BotReaction handleNonCommand(Message message) {
        long timer = System.nanoTime()-exampleGenerator.getTimer();
        String interval = MyMath.toReadableTime(timer);

        if (exampleGenerator.isAnswered()) return new BotEmptyReaction();

        String msgText = message.getText().strip();
        int userAnswer;
        boolean isRight;

        try {
            userAnswer = MyString.brutalParseInt(msgText);
        } catch (NumberFormatException e) {
            return new BotEmptyReaction();
        }

        isRight = userAnswer == exampleGenerator.getAnswerInt();
        String userName = UserNameGetter.getUserName(message.getFrom());
        String textAnswer;

        if (isRight) {
            textAnswer = String.format(
                    MessageType.RIGHT_ANSWER_MESSAGE.getRandomMessage(),
                    String.valueOf(userAnswer),
                    userName,
                    interval);
            exampleGenerator.setAnswered(true);
        } else {
            textAnswer = String.format(
                    MessageType.WRONG_ANSWER_MESSAGE.getRandomMessage(),
                    String.valueOf(userAnswer),
                    userName);
        }

        try {
            UserEntity user = databaseConnector.getOrCreateUser(message.getFrom());
            ChatEntity chat = databaseConnector.getOrCreateChat(message.getChat());

            ExampleAnswerEntity exampleAnswer = new ExampleAnswerEntity()
                    .setTimestamp(System.currentTimeMillis()/1000)
                    .setChat(chat)
                    .setUser(user)
                    .setRight(isRight);
            if (isRight) exampleAnswer.setTimer(timer/1_000_000);

            databaseConnector.save(exampleAnswer);
        } catch (Exception e) {
            System.out.println("Exception while serializing example answer to database: " + e.getMessage());
        }

        return new BotMessageSender(message.getChatId().toString(), textAnswer);
    }


}
