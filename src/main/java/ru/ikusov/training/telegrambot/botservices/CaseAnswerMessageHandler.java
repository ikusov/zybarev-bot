package ru.ikusov.training.telegrambot.botservices;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Message;
import ru.ikusov.training.telegrambot.services.CaseQuestionGenerator;
import ru.ikusov.training.telegrambot.services.UserNameGetter;
import ru.ikusov.training.telegrambot.utils.Linguistic;
import ru.ikusov.training.telegrambot.utils.MessageType;

@Component
public class CaseAnswerMessageHandler extends NonCommandMessageHandler {
    @Autowired
    private CaseQuestionGenerator caseQuestionGenerator;

    @Override
    public BotReaction handleNonCommand(Message message) {
        if (caseQuestionGenerator.isAnswered()) return new BotEmptyReaction();

        String chatId = message.getChatId().toString();
        var topicId = message.getMessageThreadId();
        String userName = UserNameGetter.getUserName(message.getFrom());

        int testResult = caseQuestionGenerator.testUserAnswer(message.getText());
        if (testResult != 0) {
            log(message);
        }
        return switch (testResult) {
            case 0 -> new BotEmptyReaction();
            case -1 -> new BotMessageSender(chatId,
                    topicId,
                    String.format(
                            MessageType.WRONG_CASE_MESSAGE.getRandomMessage(),
                            userName
                    )
            );
            case 1 -> new BotMessageSender(chatId,
                    topicId,
                    String.format(
                            MessageType.HALF_CASE_MESSAGE.getRandomMessage(),
                            userName
                    )
            );
            case 2 -> new BotMessageSender(chatId,
                    topicId,
                    String.format(
                            MessageType.RIGHT_CASE_MESSAGE.getRandomMessage(),
                            userName
                    ));
            default -> new BotMessageSender(chatId,
                    topicId,
                    String.format(
                            MessageType.TOO_MUCH_VARIANTS_MESSAGE.getRandomMessage(),
                            -testResult,
                            Linguistic.getManulWordEnding(-testResult),
                            userName
                    ));
        };
    }
}
