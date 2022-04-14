package ru.ikusov.training.telegrambot.botservices;

import ru.ikusov.training.telegrambot.model.MyBotCommand;

import java.util.Set;

public class WordleCommandMessageHandler extends CommandMessageHandler {
    private final Set<String> commandVariants = Set.of("/wordle", "/вордли");

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
        return new BotEmptyReaction();
    }
}
