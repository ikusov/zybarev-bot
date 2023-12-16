package ru.ikusov.training.telegrambot.botservices;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.ikusov.training.telegrambot.model.CommandType;
import ru.ikusov.training.telegrambot.model.MyBotCommand;
import ru.ikusov.training.telegrambot.services.AvtftalkQuoteGetter;
import ru.ikusov.training.telegrambot.services.InternetQuoteGetter;
import ru.ikusov.training.telegrambot.services.Markdownv2QuoteGetter;
import ru.ikusov.training.telegrambot.utils.MyString;

import static ru.ikusov.training.telegrambot.MainClass.AVTFTALK_CHAT_ID;

@Component
@RequiredArgsConstructor
public class QuoteCommandMessageHandler extends CommandMessageHandler {
    private final AvtftalkQuoteGetter avtftalkQuoteGetter;

    @Override
    protected CommandType getSupportedCommandType() {
        return CommandType.QUOTE;
    }

    @Override
    public BotReaction handleCommand(MyBotCommand command) {
        Markdownv2QuoteGetter quoteGetter;

        String textAnswer;

        try {
            if (AVTFTALK_CHAT_ID.equals(command.chatId().toString())) {
                quoteGetter = avtftalkQuoteGetter;
            } else {
                quoteGetter = new InternetQuoteGetter();
            }
            textAnswer = quoteGetter.getMarkdownv2FormattedQuote(command.params());
        } catch (Exception e) {
            textAnswer = MyString.markdownv2Format(e.getMessage());
        }

        return new BotFormattedMessageSender(command.chatId(), command.topicId(), textAnswer);
    }
}
