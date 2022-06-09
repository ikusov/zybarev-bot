package ru.ikusov.training.telegrambot.botservices;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Message;
import ru.ikusov.training.telegrambot.model.ChatEntity;
import ru.ikusov.training.telegrambot.model.ExampleAnswerEntity;
import ru.ikusov.training.telegrambot.model.UserEntity;
import ru.ikusov.training.telegrambot.repository.DatabaseConnector;
import ru.ikusov.training.telegrambot.services.ExampleAnswerMessageGenerator;
import ru.ikusov.training.telegrambot.services.ExampleGenerator;
import ru.ikusov.training.telegrambot.utils.MyMath;
import ru.ikusov.training.telegrambot.utils.MyString;

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
        String textAnswer;
        int score;

        int userAnswer, rightAnswer;
        boolean isRight;

        UserEntity user;
        ChatEntity chat;
        ExampleAnswerEntity exampleAnswer;


        if (exampleGenerator.isAnswered()) {
            return new BotEmptyReaction();
        }

        try {
            userAnswer = MyString.brutalParseInt(message.getText().strip());
            rightAnswer = exampleGenerator.getAnswerInt();

            isRight = (userAnswer == rightAnswer);

            user = databaseConnector.getOrCreateUser(message.getFrom());
            chat = databaseConnector.getOrCreateChat(message.getChat());

            var exampleAnswerMessageGenerator =
                    new ExampleAnswerMessageGenerator
                            (databaseConnector, user, chat, userAnswer,
                                    rightAnswer, (int)(timer/1_000_000_000));
            textAnswer = exampleAnswerMessageGenerator.generate();
            score = exampleAnswerMessageGenerator.getExampleScore();

            exampleAnswer = new ExampleAnswerEntity()
                    .setTimestamp(System.currentTimeMillis()/1000)
                    .setChat(chat)
                    .setUser(user)
                    .setRight(isRight);

            if (isRight) {
                exampleAnswer.setTimer(timer/1_000_000).setScore(score);
                exampleGenerator.setAnswered(true);
            }

            databaseConnector.save(exampleAnswer);
        } catch (Exception e) {
            System.out.println("Exception while serializing example answer to database: " + e.getMessage());
            return new BotEmptyReaction();
        }


//        String userName = UserNameGetter.getUserName(message.getFrom());
//
//        long userId = user.getId(),
//                chatId = chat.getId();
//
//        if (isRight) {
//            textAnswer = String.format(
//                    MessageType.RIGHT_ANSWER_MESSAGE.getRandomMessage(),
//                    String.valueOf(userAnswer),
//                    userName,
//                    interval);
//            exampleGenerator.setAnswered(true);
//        } else {
//            textAnswer = String.format(
//                    MessageType.WRONG_ANSWER_MESSAGE.getRandomMessage(),
//                    String.valueOf(userAnswer),
//                    userName);
//        }
//
        return new BotMessageSender(message.getChatId().toString(), textAnswer);
    }


}
