package ru.ikusov.training.telegrambot.aspect;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import ru.ikusov.training.telegrambot.logging.SaveLogger;
import ru.ikusov.training.telegrambot.logging.UpdateLogger;
import ru.ikusov.training.telegrambot.model.CommonEntity;

//TODO: выпилить после организации нормального логирования
@Deprecated
@Component
@Aspect
public class LoggingAspect {
    @Autowired
    private UpdateLogger updateLogger;
    @Autowired
    private SaveLogger saveLogger;

    @Before("execution(* *.save(*))")
    public void beforeSaveAdvice(JoinPoint joinPoint) {
        CommonEntity c = (CommonEntity)joinPoint.getArgs()[0];
        saveLogger.log(c);
    }
}
