package ru.ikusov.training.telegrambot.botservices;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import ru.ikusov.training.telegrambot.model.MyBotCommand;
import ru.ikusov.training.telegrambot.services.AvtftalkQuoteGetter;
import ru.ikusov.training.telegrambot.services.InternetQuoteGetter;
import ru.ikusov.training.telegrambot.services.Markdownv2QuoteGetter;
import ru.ikusov.training.telegrambot.utils.MyString;

import java.util.Set;

import static ru.ikusov.training.telegrambot.MainClass.AVTFTALK_CHAT_ID;

@Component
@Order(110)
public class QuoteCommandMessageHandler extends CommandMessageHandler {
    private final Set<String> commandVariants = Set.of("/quote", "/q", "/цитата", "/ц", "/й");

    @Autowired
    AvtftalkQuoteGetter avtftalkQuoteGetter;

    @Override
    protected void addHelp() {
        String help = commandVariants.stream().reduce((s1, s2) -> s1 + ", " + s2).orElse("");
        help += " - умная цитата из интернета или (если вам повезло с чатиком) какая-то цитата из старого IRС-канала. Во втором случае можно передать параметром номер цитаты.\n";
        helpString = help + helpString;
    }

    @Override
    protected Set<String> getCommandVariants() {
        return commandVariants;
    }

    @Override
    public BotReaction handleCommand(MyBotCommand command) {
        Markdownv2QuoteGetter quoteGetter;

        String textAnswer;

        try {
            if(command.getChatId().equals(AVTFTALK_CHAT_ID)) {
                quoteGetter = avtftalkQuoteGetter;
            } else {
                quoteGetter = new InternetQuoteGetter();
            }
            textAnswer = quoteGetter.getMarkdownv2FormattedQuote(command.getParams());
        } catch (Exception e) {
            textAnswer = MyString.markdownv2Format(e.getMessage());
        }

        return new BotFormattedMessageSender(command.getChatId(), command.getTopicId(), textAnswer);
    }
}
