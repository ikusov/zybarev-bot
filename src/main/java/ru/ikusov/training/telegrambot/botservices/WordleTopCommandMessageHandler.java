package ru.ikusov.training.telegrambot.botservices;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import ru.ikusov.training.telegrambot.model.MyBotCommand;
import ru.ikusov.training.telegrambot.services.wordle.WordleStatService;

import java.util.Set;

import static ru.ikusov.training.telegrambot.utils.MyString.markdownv2Format;

@Slf4j
@Component
@Order(183)
@RequiredArgsConstructor
public class WordleTopCommandMessageHandler extends CommandMessageHandler {
    private static final int DEFAULT_TOP_NUMBER = 10;

    private final WordleStatService wordleStatService;

    @Override
    protected Set<String> getCommandVariants() {
        return Set.of("/wordletop", "/wordtop", "/словатоп");
    }

    @Override
    protected String getHelpString() {
        return "Выводит топ пользователей по количеству очков в игре Wordle. " +
                "Параметр (от 3 до 100) задаёт количество элементов списка (по умолчанию 10)";
    }

    @Override
    public BotReaction handleCommand(MyBotCommand command) {
        String chatId = command.getChatId().toString();
        var topicId = command.getTopicId();

        int topNumber = fetchTopNumber(command);

        StringBuilder fMsg = new StringBuilder(1024);
        try {
            var topList = wordleStatService.getTop(command.getChat(), topNumber);
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
        String topNumberString = command.getParams().split(" ")[0].strip();
        try {
            return Integer.parseInt(topNumberString);
        } catch (NumberFormatException e) {
            return DEFAULT_TOP_NUMBER;
        }
    }
}
