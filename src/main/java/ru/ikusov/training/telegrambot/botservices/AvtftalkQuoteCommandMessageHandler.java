package ru.ikusov.training.telegrambot.botservices;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import ru.ikusov.training.telegrambot.model.MyBotCommand;
import ru.ikusov.training.telegrambot.services.AvtftalkQuoteGetter;
import ru.ikusov.training.telegrambot.services.QuoteGetter;
import ru.ikusov.training.telegrambot.utils.MyString;

import java.util.Set;

import static ru.ikusov.training.telegrambot.utils.MyMath.r;

@Component
@Order(120)
public class AvtftalkQuoteCommandMessageHandler extends CommandMessageHandler {
    private final Set<String> commandVariants = Set.of("/quote", "/q", "/цитата", "/ц", "/й");

    @Autowired
    AvtftalkQuoteGetter quoteGetter;

    @Override
    protected Set<String> getCommandVariants() {
        return commandVariants;
    }

    @Override
    public BotReaction handleCommand(MyBotCommand command) {
        if (!commandVariants.contains(command.getCommand().toLowerCase())) return null;

        String textAnswer;
        String params = command.getParams();

        try {
            int number = params.equals("") ? r(72)+1 :
                    MyString.brutalParseInt(command.getParams());
            textAnswer = quoteGetter.getMarkdownv2FormattedQuote(number);
        } catch (NumberFormatException e) {
            textAnswer = "Не могу понять, что за номер цитаты " + params;
        } catch (Exception e) {
            textAnswer = e.getMessage();
        }

        return new BotFormattedMessageSender(command.getChatId(), textAnswer);
    }
}
