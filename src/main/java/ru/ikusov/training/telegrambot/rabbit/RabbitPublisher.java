package ru.ikusov.training.telegrambot.rabbit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.logging.ErrorManager;

public class RabbitPublisher {
    private final Logger log = LoggerFactory.getLogger(this.getClass());

    private final String RABBIT_URI_ENV_NAME = "CLOUDAMQP_URL";

    public void start() {
        String uriString = System.getenv(RABBIT_URI_ENV_NAME);
        if (uriString == null) {
            log.error("No way to get system environment variable named {}", RABBIT_URI_ENV_NAME);
            return;
        }
        log.info("Rabbit URI has been getted from system env {}! It is {}!", RABBIT_URI_ENV_NAME, uriString);
    }
}
