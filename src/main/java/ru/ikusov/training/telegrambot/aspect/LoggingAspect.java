package ru.ikusov.training.telegrambot.aspect;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import ru.ikusov.training.telegrambot.model.MyBotCommand;
import ru.ikusov.training.telegrambot.services.UserNameGetter;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;

@Component
@Aspect
public class LoggingAspect {
    @Before("execution(* *.handleUpdate(*))")
    public void beforeHandleCommandAdvice(JoinPoint joinPoint) {

        Object[] args = joinPoint.getArgs();
//        Arrays.stream(args).forEach(System.out::println);
//
        String logInfo =
        Arrays.stream(args)
                .filter(arg -> arg instanceof Update)
                .filter(arg -> ((Update) arg).hasMessage() && ((Update) arg).getMessage().hasText())
                .map(arg -> ((Update) arg).getMessage())
                .map(arg -> ((Message)arg)).map(arg ->
                    String.format("%s: ChatId: %s, UserName: %s, UserId: %s, Text: %s\n",
                            LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")),
                            arg.getChatId(),
                            UserNameGetter.getUserName(arg.getFrom()),
                            arg.getFrom().getId(),
                            arg.getText()
                            ))
                .reduce((arg1, arg2) -> arg1 + arg2)
                .get();

        System.out.println(logInfo);
    }
}
