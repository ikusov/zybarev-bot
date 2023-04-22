package ru.ikusov.training.telegrambot.logging;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.User;
import org.telegram.telegrambots.meta.api.objects.inlinequery.InlineQuery;
import ru.ikusov.training.telegrambot.services.UserNameGetter;

import java.util.List;

@Component
public class UpdateLogger {
    private final Logger log = LoggerFactory.getLogger(this.getClass());

    public void log(Update update) {
        String logMsg = "";
        if (update.hasMessage()) {
            Message message = update.getMessage();
            logMsg += String.format("ChatId: %d, UserName: %s, UserId: %d",
                    message.getChatId(),
                    UserNameGetter.getUserName(message.getFrom()),
                    message.getFrom().getId());
            if (message.hasText()) {
                logMsg += String.format(", Text: %s", message.getText());
            } else if(!message.getNewChatMembers().isEmpty()) {
                List<User> newChatMembers = message.getNewChatMembers();
                logMsg += String.format(", Chat members added: %s",
                        newChatMembers.stream()
                                .map(UserNameGetter::getUserName)
                                .reduce((s1, s2) -> s1 + ", " + s2)
                                .orElse("")
                );
            } else if(message.getLeftChatMember() != null) {
                logMsg += String.format(", Chat member leaved: %s",
                        UserNameGetter.getUserName(message.getLeftChatMember()));
            } else if(message.getCaption() != null) {
                logMsg += String.format(", Sent any document with caption: %s", message.getCaption());
            } else if(message.hasPhoto()) {
                logMsg += " sent picture.";
            } else if(message.hasVideo()) {
                logMsg += " sent vidoe.";
            } else if(message.hasSticker()) {
                logMsg += " sent sticke.";
            }
            else {
                logMsg += " Any message with no text: " + message;
            }
        } else {
            if(update.hasInlineQuery()) {
                InlineQuery inlineQuery = update.getInlineQuery();
                logMsg += String.format(" Inline query id: %s, UserName: %s, UserId: %d, Text: %s",
                        inlineQuery.getId(),
                        UserNameGetter.getUserName(inlineQuery.getFrom()),
                        inlineQuery.getFrom().getId(),
                        inlineQuery.getQuery());
            } else {
                logMsg += " any update received";
            }
        }

        log.info(logMsg);
    }
}
