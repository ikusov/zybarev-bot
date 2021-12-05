package ru.ikusov.training.telegrambot.botservices;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Message;
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
                    userName
            );
        }

        return new BotMessageSender(message.getChatId().toString(), textAnswer);
    }
}
