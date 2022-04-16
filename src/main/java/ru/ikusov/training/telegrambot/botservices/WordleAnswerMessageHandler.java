package ru.ikusov.training.telegrambot.botservices;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Message;
import ru.ikusov.training.telegrambot.model.ChatEntity;
import ru.ikusov.training.telegrambot.model.ExampleAnswerEntity;
import ru.ikusov.training.telegrambot.model.UserEntity;
import ru.ikusov.training.telegrambot.services.DatabaseConnector;
import ru.ikusov.training.telegrambot.services.ExampleAnswerMessageGenerator;
import ru.ikusov.training.telegrambot.services.ExampleGenerator;
import ru.ikusov.training.telegrambot.services.wordle.WordleService;
import ru.ikusov.training.telegrambot.utils.MyMath;
import ru.ikusov.training.telegrambot.utils.MyString;

import java.util.Locale;

@Component
@Order(20)
public class WordleAnswerMessageHandler extends NonCommandMessageHandler {
    private final WordleService wordleService;

    @Autowired
    public WordleAnswerMessageHandler(WordleService wordleService) {
        this.wordleService = wordleService;
    }

    @Override
    public BotReaction handleNonCommand(Message message) {
        String text = message.getText();
        if (!MyString.isWordleAnswer(text)) {
            return new BotEmptyReaction();
        }

        text = text.toLowerCase();
        String textAnswer;
        try {
            textAnswer =
                    wordleService.checkWord(text);
        } catch (Exception e) {
            System.err.println("Any exception: " + e.getMessage());
            return new BotEmptyReaction();
        }

        return textAnswer.equals("")
                ? new BotEmptyReaction()
                : new BotFormattedMessageSender(message.getChatId().toString(), textAnswer);
    }
}
