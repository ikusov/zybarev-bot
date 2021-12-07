package ru.ikusov.training.telegrambot.botservices;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Message;
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
        if (exampleGenerator.isAnswered()) return null;

        String msgText = message.getText().strip();
        int userAnswer;

        try {
            userAnswer = MyString.brutalParseInt(msgText);
        } catch (NumberFormatException e) {
            return null;
        }

        String userAnswerString = String.valueOf(userAnswer);
        int rightAnswer = exampleGenerator.getAnswerInt();

        String userName = UserNameGetter.getUserName(message.getFrom());
        long userId = message.getFrom().getId();

        String textAnswer;

        if (userAnswer == rightAnswer) {
            String interval = MyMath.toReadableTime(System.nanoTime()-exampleGenerator.getTimer());
            textAnswer = String.format(
                    MessageType.RIGHT_ANSWER_MESSAGE.getRandomMessage(), 
                    userAnswerString,
                    userName,
                    interval);
            exampleGenerator.setAnswered(true);
        } else {
//            textAnswer = RandomMessageGenerator.generate(RandomMessageGenerator.MessageType.WRONG_ANSWER_MESSAGE,
//                                                            String.valueOf(userAnswer),
//                                                            userName);
            textAnswer = String.format(
                    MessageType.WRONG_ANSWER_MESSAGE.getRandomMessage(),
                    userAnswerString,
                    userName);
        }

        try {
            UserEntity user = databaseConnector.getById(UserEntity.class, userId);
            if (user==null) {
                user = new UserEntity(message.getFrom());

                databaseConnector.save(user);

                //for testing purposes only
                System.out.println("User saved to database: " + user);
            } else {

                //for testing purposes only
                System.out.println("User get from database: " + user);
            }
        } catch (Exception e) {
            System.out.println("Exception while serializing example answer to database: " + e.getMessage());
        }

        return new BotMessageSender(message.getChatId().toString(), textAnswer);
    }
}
