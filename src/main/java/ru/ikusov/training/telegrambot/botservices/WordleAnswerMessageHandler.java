package ru.ikusov.training.telegrambot.botservices;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Message;
import redis.clients.jedis.exceptions.JedisConnectionException;
import ru.ikusov.training.telegrambot.repository.DatabaseConnector;
import ru.ikusov.training.telegrambot.services.wordle.WordleService;

@Component
@Order(20)
public class WordleAnswerMessageHandler extends NonCommandMessageHandler {
    private final Logger log = LoggerFactory.getLogger(this.getClass());

    private final WordleService wordleService;
    @Autowired
    public WordleAnswerMessageHandler(WordleService wordleService) {
        this.wordleService = wordleService;
    }

    @Override
    public BotReaction handleNonCommand(Message message) {
        String text = message.getText();
        boolean isWordleAnswer = false;

        //check message text if it may be wordle answer
        try {
            isWordleAnswer = wordleService.isWordleAnswer(text, message.getChatId());
        } catch (Exception e) {
            log.error("Error while checking isWordleAnswer: {}", e.getMessage());
        }

        if (!isWordleAnswer) {
            return new BotEmptyReaction();
        }

        //move this conversion to wordle service
//        text = WordleUtils.toWordleString(text);
        String textAnswer;
        try {
            textAnswer =
                    wordleService.checkWord(text, message.getFrom(), message.getChatId());
        } catch (JedisConnectionException e) {
            log.error("Jedis connection exception: " + e.getMessage());
            return new BotMessageSender(message.getChatId().toString(),
                    "Ошибка сети! Попробуйте ещё раз.");
        } catch (Exception e) {
//            System.err.println("Any exception: " + e.getMessage());
            log.error("Any exception: " + e.getMessage());
            return new BotEmptyReaction();
        }

        return textAnswer.isEmpty()
                ? new BotEmptyReaction()
                : new BotFormattedMessageSender(message.getChatId().toString(), textAnswer);
    }
}
