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
        String userName = UserNameGetter.getUserName(message.getFrom());

        int testResult = caseQuestionGenerator.testUserAnswer(message.getText());
        if (testResult != 0) {
            log(message);
        }
        switch (testResult) {
            case 0:
                return new BotEmptyReaction();
            case -1:
                return new BotMessageSender(chatId,
                        String.format(
                                MessageType.WRONG_CASE_MESSAGE.getRandomMessage(),
                                userName
                        )
                );
            case 1:
                return new BotMessageSender(chatId,
                        String.format(
                                MessageType.HALF_CASE_MESSAGE.getRandomMessage(),
                                userName
                        )
                );
            case 2:
                return new BotMessageSender(chatId,
                        String.format(
                                MessageType.RIGHT_CASE_MESSAGE.getRandomMessage(),
                                userName
                        ));
            default:
                return new BotMessageSender(chatId,
                        String.format(
                                MessageType.TOO_MUCH_VARIANTS_MESSAGE.getRandomMessage(),
                                String.valueOf(-testResult),
                                Linguistic.getManulWordEnding(-testResult),
                                userName
                        ));
        }
    }
}
