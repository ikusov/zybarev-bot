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
import ru.ikusov.training.telegrambot.services.ExampleProvider;
import ru.ikusov.training.telegrambot.utils.MyString;

@Component
@Order(10)
public class ExampleAnswerMessageHandler extends NonCommandMessageHandler {
    @Autowired
    DatabaseConnector databaseConnector;
    @Autowired
    private ExampleProvider exampleProvider;

    @Override
    public BotReaction handleNonCommand(Message message) {
        long timer = System.nanoTime() - exampleProvider.getTimer();
        String textAnswer;
        int score;

        int userAnswer, rightAnswer;
        boolean isRight;

        UserEntity user;
        ChatEntity chat;
        ExampleAnswerEntity exampleAnswer;


        try {
            //persisting user and chat to database no matter if message has example answer or not
            //todo: убрать здесь сохранение чата в БД, т.к. по факту не нужен он в БД
            //todo: выпилить таблицу чата из БД
            user = databaseConnector.getOrCreateUser(message.getFrom());
            chat = databaseConnector.getOrCreateChat(message.getChat());

            if (exampleProvider.isAnswered()) {
                return new BotEmptyReaction();
            }

            userAnswer = MyString.brutalParseInt(message.getText());
            rightAnswer = exampleProvider.getAnswerInt();

            isRight = (userAnswer == rightAnswer);

            var exampleAnswerMessageGenerator =
                    new ExampleAnswerMessageGenerator
                            (databaseConnector, user, chat, userAnswer,
                                    rightAnswer, (int) (timer / 1_000_000_000));
            textAnswer = exampleAnswerMessageGenerator.generate();
            score = exampleAnswerMessageGenerator.getExampleScore();

            //todo: из ExampleAnswerEntity выпилить ChatEntity, оставить только id чата
            //todo: но id чата теперь должен быть составной Chat ID + Group ID
            exampleAnswer = new ExampleAnswerEntity()
                    //todo: эту штучку можно заменить на @CreationTimestamp
                    .setTimestamp(System.currentTimeMillis() / 1000)
                    .setChat(chat)
                    .setUser(user)
                    .setRight(isRight);

            if (isRight) {
                exampleAnswer.setTimer(timer / 1_000_000).setScore(score);
                exampleProvider.setAnswered(true);
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
