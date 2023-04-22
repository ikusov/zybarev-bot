package ru.ikusov.training.telegrambot.logging;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import ru.ikusov.training.telegrambot.model.CommonEntity;

@Component
public class SaveLogger {
    private final Logger log = LoggerFactory.getLogger(this.getClass());

    public void log(CommonEntity commonEntity) {
        String logInfo = "";
        logInfo += "attempt to save object of class " + commonEntity.getClass().getName() + " to da tabase.";

        log.info(logInfo);
    }
}
