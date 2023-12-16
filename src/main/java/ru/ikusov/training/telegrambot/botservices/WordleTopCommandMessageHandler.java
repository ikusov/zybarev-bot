package ru.ikusov.training.telegrambot.botservices;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.ikusov.training.telegrambot.model.CommandType;
import ru.ikusov.training.telegrambot.model.MyBotCommand;
import ru.ikusov.training.telegrambot.services.wordle.WordleStatService;

import static ru.ikusov.training.telegrambot.utils.MyString.markdownv2Format;

@Slf4j
@Component
@RequiredArgsConstructor
public class WordleTopCommandMessageHandler extends CommandMessageHandler {
    private static final int DEFAULT_TOP_NUMBER = 10;

    private final WordleStatService wordleStatService;

    @Override
    protected CommandType getSupportedCommandType() {
        return CommandType.WORDLETOP;
    }

    @Override
    public BotReaction handleCommand(MyBotCommand command) {
        String chatId = command.chatId().toString();
        var topicId = command.topicId();

        int topNumber = fetchTopNumber(command);

        StringBuilder fMsg = new StringBuilder(1024);
        try {
            var topList = wordleStatService.getTop(command.chat(), topNumber);
            for (String s : topList) {
                fMsg.append(markdownv2Format(s)).append("\n");
            }
            return new BotFormattedMessageSender(chatId, topicId, fMsg.toString());
        } catch (Exception e) {
            log.error("Error while wordle stat handling!", e);
            return new BotMessageSender(chatId, topicId, "Неизвестная ошибка! Попробуйте ещё раз.");
        }
    }

    private int fetchTopNumber(MyBotCommand command) {
        String topNumberString = command.params().split(" ")[0].strip();
        try {
            return Integer.parseInt(topNumberString);
        } catch (NumberFormatException e) {
            return DEFAULT_TOP_NUMBER;
        }
    }
}
