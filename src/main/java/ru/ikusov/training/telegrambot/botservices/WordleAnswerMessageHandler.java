package ru.ikusov.training.telegrambot.botservices;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Message;
import ru.ikusov.training.telegrambot.services.wordle.WordleService;

import static ru.ikusov.training.telegrambot.MainClass.IS_TEST_MODE;
import static ru.ikusov.training.telegrambot.MainClass.TEST_CHAT_ID;

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
    //устранить протекание бизнес-логики в логику бота
    //по идее этот метод это аналог метода контроллера в REST-API, т.е. сюда мы отправляем данные запроса
    //а отсюда мы ожидаем данные ответа
    //т.е. в этом методе должен быть только вызов метода сервиса и не более того
    public BotReaction handleNonCommand(Message message) {
        if (IS_TEST_MODE && !message.getChatId().equals(TEST_CHAT_ID)) {
            return new BotEmptyReaction();
        }

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

        String textAnswer;
        try {
            textAnswer =
                    wordleService.checkWord(text, message.getFrom(), message.getChatId());
        } catch (Exception e) {
            log.error("Any exception: " + e.getMessage());
            return new BotEmptyReaction();
        }

        if (textAnswer.isEmpty()) {
            return new BotEmptyReaction();
        } else {
            log(message);
            return new BotFormattedMessageSender(
                    message.getChatId().toString(),
                    message.getMessageThreadId(),
                    textAnswer
            );
        }
    }
}
