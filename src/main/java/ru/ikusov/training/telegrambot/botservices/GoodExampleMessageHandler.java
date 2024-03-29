package ru.ikusov.training.telegrambot.botservices;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Message;
import ru.ikusov.training.telegrambot.services.ExampleProvider;
import ru.ikusov.training.telegrambot.utils.MessageType;
import ru.ikusov.training.telegrambot.utils.MyString;

@Component
@Order(15)
public class GoodExampleMessageHandler extends NonCommandMessageHandler {
    @Autowired
    ExampleProvider exampleProvider;

    public BotReaction handleNonCommand(Message message) {
        if (exampleProvider.isAnswered()) return new BotEmptyReaction();

        String msgText = MyString.trimPunctuationMarksInclusive(message.getText());

        if (msgText.equalsIgnoreCase("хороший пример")) {
            String textAnswer =
//                    RandomMessageGenerator.generate(RandomMessageGenerator.MessageType.GOOD_EXAMPLE_ANSWER_MESSAGE);
                    MessageType.GOOD_EXAMPLE_ANSWER_MESSAGE.getRandomMessage();

            log(message);
            return new BotMessageSender(message.getChatId().toString(), message.getMessageThreadId(), textAnswer);
        } else return new BotEmptyReaction();
    }
}
