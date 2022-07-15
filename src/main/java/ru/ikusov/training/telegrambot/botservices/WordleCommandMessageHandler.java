package ru.ikusov.training.telegrambot.botservices;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import ru.ikusov.training.telegrambot.model.MyBotCommand;
import ru.ikusov.training.telegrambot.services.wordle.WordleService;

import java.util.Set;

@Component
@Order(180)
public class WordleCommandMessageHandler extends CommandMessageHandler {
    private final Logger log = LoggerFactory.getLogger(this.getClass());
    private final Set<String> commandVariants = Set.of("/wordle", "/words", "/слова");

    private final WordleService wordleService;

    @Autowired
    public WordleCommandMessageHandler(WordleService wordleService) {
        this.wordleService = wordleService;
    }

    @Override
    protected void addHelp() {
        String help = commandVariants.stream().reduce((s1, s2) -> s1 + ", " + s2).orElse("");
        help += " - Игра в слова по типу Wordle. Параметром можно задать длину слова (от 4 до 8 символов)\n";
        helpString = help + helpString;
    }

    @Override
    protected Set<String> getCommandVariants() {
        return commandVariants;
    }

    @Override
    public BotReaction handleCommand(MyBotCommand command) {
        String fMsg;
        String chatId = command.getChatId();
        var wordLen = getWordLength(command.getParams());

        try {
            fMsg = wordleService.startGame(Long.valueOf(command.getChatId()), wordLen);
        } catch (Exception e) {
            log.error("Error while start game handling: {}", e.getMessage());
            return new BotMessageSender(chatId, "Неизвестная ошибка! Попробуйте ещё раз.");
        }
        return new BotFormattedMessageSender(command.getChatId(), fMsg);
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
