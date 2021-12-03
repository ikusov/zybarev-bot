package ru.ikusov.training.telegrambot.botservices;

import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import ru.ikusov.training.telegrambot.model.MyBotCommand;
import ru.ikusov.training.telegrambot.services.QuoteGetter;

import java.util.Set;

//@Component
//@Order(110)
public class QuoteCommandMessageHandler extends CommandMessageHandler {
    private final Set<String> commandVariants = Set.of("/quote", "/q", "/цитата", "/ц", "/й");

    @Override
    protected Set<String> getCommandVariants() {
        return commandVariants;
    }

    @Override
    public BotReaction handleCommand(MyBotCommand command) {
        if (!commandVariants.contains(command.getCommand().toLowerCase())) return null;

        QuoteGetter quoteGetter;
        String textAnswer;

        try {
            quoteGetter = new QuoteGetter();
            textAnswer = quoteGetter.getMarkdownv2FormattedQuote();
        } catch (Exception e) {
            textAnswer = e.getMessage();
        }

        return new BotFormattedMessageSender(command.getChatId(), textAnswer);
    }
}
