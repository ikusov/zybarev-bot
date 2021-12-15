package ru.ikusov.training.telegrambot.botservices;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.User;
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

        int userAnswer;
        boolean isRight;

        UserEntity user;
        ChatEntity chat;
        ExampleAnswerEntity exampleAnswer;


        if (exampleGenerator.isAnswered()) {
            return new BotEmptyReaction();
        }

        try {
            userAnswer = MyString.brutalParseInt(message.getText().strip());
            isRight = (userAnswer == exampleGenerator.getAnswerInt());

            user = databaseConnector.getOrCreateUser(message.getFrom());
            chat = databaseConnector.getOrCreateChat(message.getChat());
            exampleAnswer = new ExampleAnswerEntity()
                    .setTimestamp(System.currentTimeMillis()/1000)
                    .setChat(chat)
                    .setUser(user)
                    .setRight(isRight);
            if (isRight) exampleAnswer.setTimer(timer/1_000_000);

            databaseConnector.save(exampleAnswer);
        } catch (Exception e) {
            System.out.println("Exception while serializing example answer to database: " + e.getMessage());
            return new BotEmptyReaction();
        }


        /* todo: Сообщение юзера - число. Это ответ на пример. Он может быть неправильный:
            значит, надо проверить, насколько он близок к правильному, и выдать сообщение
            навроде "so close...", "десяточка обиженно смотрит на тебя",
            "соточка обиженно смотрит на тебя" и т.п.
            Он может быть правильный: значит выдаём сообщение, мол победитель тот-то, так держать,
            затем перечисляем бонусы.
        */


        String userName = UserNameGetter.getUserName(message.getFrom());
        String textAnswer;

        long userId = user.getId(),
                chatId = chat.getId();

        if (isRight) {

        }

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

        return new BotMessageSender(message.getChatId().toString(), textAnswer);
    }


}
