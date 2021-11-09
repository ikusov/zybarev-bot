package ru.ikusov.training.telegrambot.botservices;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Message;
import ru.ikusov.training.telegrambot.services.ExampleGenerator;
import ru.ikusov.training.telegrambot.utils.MyString;
import ru.ikusov.training.telegrambot.utils.RandomMessageGenerator;

import java.util.Locale;

@Component
@Order(15)
public class GoodExampleMessageHandler extends NonCommandMessageHandler {
    @Autowired
    ExampleGenerator exampleGenerator;

    public BotReaction handleNonCommand(Message message) {
        if (exampleGenerator.isAnswered()) return null;

        String msgText = MyString.trimPunctuationMarksInclusive(message.getText());

        if (msgText.equalsIgnoreCase("хороший пример")) {
            String textAnswer = RandomMessageGenerator.generate(RandomMessageGenerator.MessageType.GOOD_EXAMPLE_ANSWER_MESSAGE);
            return new BotMessageSender(message.getChatId().toString(), textAnswer);
        } else return null;
    }
}
