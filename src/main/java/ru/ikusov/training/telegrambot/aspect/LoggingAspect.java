package ru.ikusov.training.telegrambot.aspect;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.User;
import org.telegram.telegrambots.meta.api.objects.inlinequery.InlineQuery;
import ru.ikusov.training.telegrambot.model.CommonEntity;
import ru.ikusov.training.telegrambot.services.UserNameGetter;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Component
@Aspect
public class LoggingAspect {
    @Before("execution(* *.save(*))")
    public void beforeSaveAdvice(JoinPoint joinPoint) {
        CommonEntity c = (CommonEntity)joinPoint.getArgs()[0];
        String logInfo = String.format("%s: ",
                LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
        logInfo += "attempt to save object of class " + c.getClass().getName() + " to da tabase.";

        System.out.println(logInfo);
    }

    @Before("execution(* *.handleUpdate(*))")
    public void beforeHandleUpdateAdvice(JoinPoint joinPoint) {

        Update update = (Update)joinPoint.getArgs()[0];
//        Arrays.stream(args).forEach(System.out::println);
//
        String logInfo = String.format("%s: ",
                LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
        if (update.hasMessage()) {
            Message message = update.getMessage();
            logInfo += String.format("ChatId: %d, UserName: %s, UserId: %d",
                                    message.getChatId(),
                                    UserNameGetter.getUserName(message.getFrom()),
                                    message.getFrom().getId());
            if (message.hasText()) {
                logInfo += String.format(", Text: %s", message.getText());
            } else if(!message.getNewChatMembers().isEmpty()) {
                List<User> newChatMembers = message.getNewChatMembers();
                logInfo += String.format(", Chat members added: %s",
                                newChatMembers.stream()
                                .map(UserNameGetter::getUserName)
                                .reduce((s1, s2) -> s1 + ", " + s2)
                                        .get()
                                );
            } else if(message.getLeftChatMember() != null) {
                logInfo += String.format(", Chat member leaved: %s",
                        UserNameGetter.getUserName(message.getLeftChatMember()));
            } else if(message.getCaption() != null) {
                logInfo += String.format(", Sent any document with caption: %s", message.getCaption());
            } else if(message.hasPhoto()) {
                logInfo += " sent picture.";
            } else if(message.hasVideo()) {
                logInfo += " sent vidoe.";
            } else if(message.hasSticker()) {
                logInfo += " sent sticke.";
            }
            else {
                logInfo += " Any message with no text: " + message;
            }
        } else {
            if(update.hasInlineQuery()) {
                InlineQuery inlineQuery = update.getInlineQuery();
                logInfo += String.format(" Inline query id: %s, UserName: %s, UserId: %d, Text: %s",
                        inlineQuery.getId(),
                        UserNameGetter.getUserName(inlineQuery.getFrom()),
                        inlineQuery.getFrom().getId(),
                        inlineQuery.getQuery());
            } else {
                logInfo += " any update received";
            }
        }

        System.out.println(logInfo);
    }
}
