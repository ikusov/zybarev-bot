package ru.ikusov.training.telegrambot.botservices;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import ru.ikusov.training.telegrambot.model.MyBotCommand;
import ru.ikusov.training.telegrambot.services.wordle.WordleStatService;

import java.util.Set;

import static ru.ikusov.training.telegrambot.utils.MyString.markdownv2Format;

@Component
@Order(183)
public class WordleTopCommandMessageHandler extends CommandMessageHandler {
    private static final int DEFAULT_TOP_NUMBER = 10;
    private final Logger log = LoggerFactory.getLogger(this.getClass());
    private final Set<String> commandVariants = Set.of("/wordletop", "/wordtop", "/словатоп");
    private final WordleStatService wordleStatService;

    @Autowired
    public WordleTopCommandMessageHandler(WordleStatService wordleStatService) {
        this.wordleStatService = wordleStatService;
    }

    @Override
    protected void addHelp() {
        String help = commandVariants.stream().reduce((s1, s2) -> s1 + ", " + s2).orElse("");
        help += " - Выводит топ пользователей по количеству очков в игре Wordle. " +
                "Параметр (от 3 до 100) задаёт количество элементов списка (по умолчанию 10).\n";
        helpString = help + helpString;
    }

    @Override
    protected Set<String> getCommandVariants() {
        return commandVariants;
    }

    @Override
    public BotReaction handleCommand(MyBotCommand command) {
        String chatId = command.getChatId();

        int topNumber = fetchTopNumber(command);

        StringBuilder fMsg = new StringBuilder(1024);
        try {
            var topList = wordleStatService.getTop(command.getChat(), topNumber);
            for (String s : topList) {
                fMsg.append(markdownv2Format(s)).append("\n");
            }
            return new BotFormattedMessageSender(chatId, fMsg.toString());
        } catch (Exception e) {
            log.error("Error while wordle stat handling!", e);
            return new BotMessageSender(chatId, "Неизвестная ошибка! Попробуйте ещё раз.");
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
