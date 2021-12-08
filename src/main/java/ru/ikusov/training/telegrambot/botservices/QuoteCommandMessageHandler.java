package ru.ikusov.training.telegrambot.botservices;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import ru.ikusov.training.telegrambot.model.MyBotCommand;
import ru.ikusov.training.telegrambot.services.AvtftalkQuoteGetter;
import ru.ikusov.training.telegrambot.services.InternetQuoteGetter;
import ru.ikusov.training.telegrambot.services.Markdownv2QuoteGetter;

import java.util.Set;

@Component
@Order(110)
public class QuoteCommandMessageHandler extends CommandMessageHandler {
    private final Set<String> commandVariants = Set.of("/quote", "/q", "/цитата", "/ц", "/й");
    private final String avtftalkChatId = "-1001306495099";

    @Autowired
    AvtftalkQuoteGetter avtftalkQuoteGetter;

    @Override
    protected Set<String> getCommandVariants() {
        return commandVariants;
    }

    @Override
    public BotReaction handleCommand(MyBotCommand command) {
//        if (!commandVariants.contains(command.getCommand().toLowerCase())) return null;
        Markdownv2QuoteGetter quoteGetter;

        String textAnswer;

        try {
            if(command.getChatId().equals(avtftalkChatId)) {
                quoteGetter = avtftalkQuoteGetter;
            } else {
                quoteGetter = new InternetQuoteGetter();
            }
            textAnswer = quoteGetter.getMarkdownv2FormattedQuote(command.getParams());
        } catch (Exception e) {
            textAnswer = e.getMessage();
        }

        return new BotFormattedMessageSender(command.getChatId(), textAnswer);
    }
}
