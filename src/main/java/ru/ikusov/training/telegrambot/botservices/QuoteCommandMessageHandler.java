package ru.ikusov.training.telegrambot.botservices;

import lombok.RequiredArgsConstructor;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import ru.ikusov.training.telegrambot.botservices.annotation.ExcludeFromHelp;
import ru.ikusov.training.telegrambot.model.MyBotCommand;
import ru.ikusov.training.telegrambot.services.AvtftalkQuoteGetter;
import ru.ikusov.training.telegrambot.services.InternetQuoteGetter;
import ru.ikusov.training.telegrambot.services.Markdownv2QuoteGetter;
import ru.ikusov.training.telegrambot.utils.MyString;

import java.util.Set;

import static ru.ikusov.training.telegrambot.MainClass.AVTFTALK_CHAT_ID;

@Component
@Order(110)
@RequiredArgsConstructor
public class QuoteCommandMessageHandler extends CommandMessageHandler {

    private final AvtftalkQuoteGetter avtftalkQuoteGetter;

    @Override
    protected Set<String> getCommandVariants() {
        return Set.of("/quote", "/q", "/цитата", "/ц", "/й");
    }

    @Override
    @ExcludeFromHelp
    protected String getHelpString() {
        return "умная цитата из интернета или (если вам повезло с чатиком) какая-то цитата из старого IRС-канала. Во втором случае можно передать параметром номер цитаты";
    }

    @Override
    public BotReaction handleCommand(MyBotCommand command) {
        Markdownv2QuoteGetter quoteGetter;

        String textAnswer;

        try {
            if (AVTFTALK_CHAT_ID.equals(command.getChatId().toString())) {
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
