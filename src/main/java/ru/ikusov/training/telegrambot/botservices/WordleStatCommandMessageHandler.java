package ru.ikusov.training.telegrambot.botservices;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.ikusov.training.telegrambot.model.CommandType;
import ru.ikusov.training.telegrambot.model.MyBotCommand;
import ru.ikusov.training.telegrambot.services.wordle.WordleStatService;

import static ru.ikusov.training.telegrambot.MainClass.IS_TEST_MODE;
import static ru.ikusov.training.telegrambot.MainClass.TEST_CHAT_ID;

@Slf4j
@Component
@RequiredArgsConstructor
public class WordleStatCommandMessageHandler extends CommandMessageHandler {
    private final WordleStatService wordleStatService;

    @Override
    protected CommandType getSupportedCommandType() {
        return CommandType.WORDLESTAT;
    }

    @Override
    public BotReaction handleCommand(MyBotCommand command) {
        String chatId = command.chatId().toString();
        var topicId = command.topicId();
        if (IS_TEST_MODE && !chatId.equals(TEST_CHAT_ID.toString())) {
            return new BotMessageSender(
                    chatId,
                    topicId,
                    "Игра недоступна в связи с проведением профилактических работ. Попробуйте позже!"
            );
        }

        String fMsg;
        try {
            fMsg = wordleStatService.getStat(command.chat(), command.user());
            return new BotFormattedMessageSender(chatId, topicId, fMsg);
        } catch (Exception e) {
            log.error("Error while wordle stat handling!", e);
            return new BotMessageSender(chatId, topicId, "Неизвестная ошибка! Попробуйте ещё раз.");
        }
    }
}
