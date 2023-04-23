package ru.ikusov.training.telegrambot.botservices;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Message;
import ru.ikusov.training.telegrambot.dao.DatabaseConnector;
import ru.ikusov.training.telegrambot.model.ChatEntity;
import ru.ikusov.training.telegrambot.model.ExampleAnswerEntity;
import ru.ikusov.training.telegrambot.model.UserEntity;
import ru.ikusov.training.telegrambot.services.ExampleAnswerMessageGenerator;
import ru.ikusov.training.telegrambot.services.ExampleGenerator;
import ru.ikusov.training.telegrambot.utils.MyString;

@Component
@Order(10)
public class ExampleAnswerMessageHandler extends NonCommandMessageHandler {
    @Autowired
    DatabaseConnector databaseConnector;
    @Autowired
    private ExampleGenerator exampleGenerator;

    @Override
    public BotReaction handleNonCommand(Message message) {
        long timer = System.nanoTime() - exampleGenerator.getTimer();
        String textAnswer;
        int score;

        int userAnswer, rightAnswer;
        boolean isRight;

        UserEntity user;
        ChatEntity chat;
        ExampleAnswerEntity exampleAnswer;


        try {
            //persisting user and chat to database no matter if message has example answer or not
            user = databaseConnector.getOrCreateUser(message.getFrom());
            chat = databaseConnector.getOrCreateChat(message.getChat());

            if (exampleGenerator.isAnswered()) {
                return new BotEmptyReaction();
            }

            userAnswer = MyString.brutalParseInt(message.getText().strip());
            rightAnswer = exampleGenerator.getAnswerInt();

            isRight = (userAnswer == rightAnswer);

            var exampleAnswerMessageGenerator =
                    new ExampleAnswerMessageGenerator
                            (databaseConnector, user, chat, userAnswer,
                                    rightAnswer, (int) (timer / 1_000_000_000));
            textAnswer = exampleAnswerMessageGenerator.generate();
            score = exampleAnswerMessageGenerator.getExampleScore();

            exampleAnswer = new ExampleAnswerEntity()
                    .setTimestamp(System.currentTimeMillis() / 1000)
                    .setChat(chat)
                    .setUser(user)
                    .setRight(isRight);

            if (isRight) {
                exampleAnswer.setTimer(timer / 1_000_000).setScore(score);
                exampleGenerator.setAnswered(true);
            }

            databaseConnector.save(exampleAnswer);
        } catch (NumberFormatException e) {
            return new BotEmptyReaction();
        } catch (Exception e) {
            log.warn("Exception while serializing example answer to database!", e);
            return new BotEmptyReaction();
        }

        log(message);
        return new BotMessageSender(message.getChatId().toString(), message.getMessageThreadId(), textAnswer);
    }


}
