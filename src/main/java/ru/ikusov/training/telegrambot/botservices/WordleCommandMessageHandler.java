package ru.ikusov.training.telegrambot.botservices;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.ikusov.training.telegrambot.model.CommandType;
import ru.ikusov.training.telegrambot.model.MyBotCommand;
import ru.ikusov.training.telegrambot.services.wordle.WordleService;
import ru.ikusov.training.telegrambot.services.wordle.WordleStatService;

import java.util.List;

import static ru.ikusov.training.telegrambot.MainClass.IS_TEST_MODE;
import static ru.ikusov.training.telegrambot.MainClass.TEST_CHAT_ID;

@Slf4j
@Component
@RequiredArgsConstructor
public class WordleCommandMessageHandler extends CommandMessageHandler {
    private final static List<String> STAT_PARAM_LIST = List.of("стат", "stat");

    private final WordleService wordleService;
    private final WordleStatService wordleStatService;

    @Override
    protected CommandType getSupportedCommandType() {
        return CommandType.WORDLE;
    }

    @Override
    public BotReaction handleCommand(MyBotCommand command) {
        Long chatId = command.chatId();
        var topicId = command.topicId();
        if (IS_TEST_MODE && !chatId.equals(TEST_CHAT_ID)) {
            return new BotMessageSender(chatId.toString(), topicId, "Игра недоступна в связи с проведением профилактических работ. Попробуйте позже!");
        }

        String fMsg;
        String commandParams = command.params().strip();

        if (isStatParam(commandParams)) {
            fMsg = wordleStatService.getStat(command.chat(), command.user());
            return new BotFormattedMessageSender(chatId.toString(), topicId, fMsg);
        }

        var wordLen = getWordLength(commandParams);

        try {
            fMsg = wordleService.startGame(command.chat(), command.user().getId(), wordLen);
        } catch (Exception e) {
            log.error("Error while start game handling: {}", e.getMessage());
            return new BotMessageSender(chatId.toString(), topicId, "Неизвестная ошибка! Попробуйте ещё раз.");
        }
        return new BotFormattedMessageSender(chatId.toString(), topicId, fMsg);
    }

    private boolean isStatParam(String commandParams) {
        return STAT_PARAM_LIST.stream()
                .map(commandParams::contains)
                .reduce((c1, c2) -> c1 || c2)
                .orElse(false);
    }

    private int getWordLength(String commandParams) {
        int wordLength = 0;
        try {
            wordLength = Integer.parseInt(commandParams.strip());
        } catch (NumberFormatException ignore) {
        }

        return wordLength;
    }
}
