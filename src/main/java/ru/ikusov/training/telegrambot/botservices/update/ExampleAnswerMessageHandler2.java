package ru.ikusov.training.telegrambot.botservices.update;

import lombok.RequiredArgsConstructor;
import org.telegram.telegrambots.meta.api.objects.Message;
import ru.ikusov.training.telegrambot.botservices.BotEmptyReaction;
import ru.ikusov.training.telegrambot.botservices.BotReaction;
import ru.ikusov.training.telegrambot.botservices.NonCommandMessageHandler;
import ru.ikusov.training.telegrambot.services.ExampleProvider;
import ru.ikusov.training.telegrambot.services.example.ExampleService;

/**
 * Обработчик ответов на арифметические примеры.
 */
//TODO: болванка нового обработчика ответов, цель: сделать его тоньше
    //TODO: в старом дофига бизнес-логики и лапшекод
//@Component
//@Order(10)
@RequiredArgsConstructor
public class ExampleAnswerMessageHandler2 extends NonCommandMessageHandler {
    private final ExampleProvider exampleProvider;
    private final ExampleService exampleService;

    @Override
    public BotReaction handleNonCommand(Message message) {
//        Optional<Integer> exampleAnswerOptional = ExampleHelper.parseExampleAnswer(message.getText());
//
//        if (exampleAnswerOptional.isEmpty() || exampleService.isAnswered(message.getChat())) {
//            return new BotEmptyReaction();
//        }
//
//        long timer = System.nanoTime() - exampleProvider.getTimer();
//        String textAnswer;
//        int score;
//
//        int userAnswer, rightAnswer;
//        boolean isRight;
//
//        UserEntity user;
//        ChatEntity chat;
//        ExampleAnswerEntity exampleAnswer;
//
//
//        try {
//        } catch (NumberFormatException e) {
//            return new BotEmptyReaction();
//        } catch (Exception e) {
//            log.warn("Exception while serializing example answer to database!", e);
//            return new BotEmptyReaction();
//        }
//
//        log(message);
//        return new BotMessageSender(message.getChatId().toString(), message.getMessageThreadId(), textAnswer);
        return new BotEmptyReaction();
    }


}
