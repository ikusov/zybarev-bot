package ru.ikusov.training.telegrambot.botservices;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import ru.ikusov.training.telegrambot.model.MyBotCommand;
import ru.ikusov.training.telegrambot.services.wordle.WordleService;

import java.util.Set;

@Component
@Order(180)
public class WordleCommandMessageHandler extends CommandMessageHandler {
    private final Set<String> commandVariants = Set.of("/wordle", "/words", "/слова");

    private final WordleService wordleService;

    @Autowired
    public WordleCommandMessageHandler(WordleService wordleService) {
        this.wordleService = wordleService;
    }

    @Override
    protected void addHelp() {
        String help = commandVariants.stream().reduce((s1, s2) -> s1 + ", " + s2).orElse("");
        help += " - Игра в слова по типу Wordle.\n";
        helpString = help + helpString;
    }

    @Override
    protected Set<String> getCommandVariants() {
        return commandVariants;
    }

    @Override
    public BotReaction handleCommand(MyBotCommand command) {
        String fMsg = wordleService.startGame(Long.valueOf(command.getChatId()));
        return new BotFormattedMessageSender(command.getChatId(), fMsg);
    }
}
