package ru.ikusov.training.telegrambot.botservices;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import ru.ikusov.training.telegrambot.model.MyBotCommand;
import ru.ikusov.training.telegrambot.services.wordle.WordleService;
import ru.ikusov.training.telegrambot.services.wordle.WordleStatService;

import java.util.List;
import java.util.Set;

import static ru.ikusov.training.telegrambot.MainClass.IS_TEST_MODE;
import static ru.ikusov.training.telegrambot.MainClass.TEST_CHAT_ID;

@Component
@Order(180)
public class WordleCommandMessageHandler extends CommandMessageHandler {
    private final static List<String> STAT_PARAM_LIST = List.of("стат", "stat");

    private final Logger log = LoggerFactory.getLogger(this.getClass());
    private final Set<String> commandVariants = Set.of("/wordle", "/words", "/слова");

    private final WordleService wordleService;
    private final WordleStatService wordleStatService;

    @Autowired
    public WordleCommandMessageHandler(WordleService wordleService, WordleStatService wordleStatService) {
        this.wordleService = wordleService;
        this.wordleStatService = wordleStatService;
    }

    @Override
    protected void addHelp() {
        String help = commandVariants.stream().reduce((s1, s2) -> s1 + ", " + s2).orElse("");
        help += " - Игра в слова по типу Wordle. Параметром можно задать длину слова (от 4 до 8 символов), " +
                "параметр 'стат' выводит статистику по игре.\n";
        helpString = help + helpString;
    }

    @Override
    protected Set<String> getCommandVariants() {
        return commandVariants;
    }

    @Override
    //todo: добавить вывод статистики угаданных пользователем слов, топ пользователей по угаданным словам,
    //todo: топ пользователей по соотношению попытки/угаданные слова
    public BotReaction handleCommand(MyBotCommand command) {
        String chatId = command.getChatId();
        if (IS_TEST_MODE && !chatId.equals(TEST_CHAT_ID.toString())) {
            return new BotMessageSender(chatId, "Игра недоступна в связи с проведением профилактических работ. Попробуйте позже!");
        }

        String fMsg;
        String commandParams = command.getParams().strip();

        if (isStatParam(commandParams)) {
            fMsg = wordleStatService.getStat(command.getChat(), command.getUser());
            return new BotFormattedMessageSender(chatId, fMsg);
        }

        var wordLen = getWordLength(commandParams);

        try {
            fMsg = wordleService.startGame(Long.valueOf(chatId), command.getUser().getId(), wordLen);
        } catch (Exception e) {
            log.error("Error while start game handling: {}", e.getMessage());
            return new BotMessageSender(chatId, "Неизвестная ошибка! Попробуйте ещё раз.");
        }
        return new BotFormattedMessageSender(chatId, fMsg);
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
