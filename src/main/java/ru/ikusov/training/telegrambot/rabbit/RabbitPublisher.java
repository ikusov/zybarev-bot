package ru.ikusov.training.telegrambot.rabbit;

import com.rabbitmq.client.ConnectionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URISyntaxException;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.util.logging.ErrorManager;

public class RabbitPublisher {
    private final static String RABBIT = "RABBITRABBITRABBIT ";
    private static final int RABBIT_PORT = 5672;

    private final Logger log = LoggerFactory.getLogger(this.getClass());

    private final String RABBIT_URI_ENV_NAME = "CLOUDAMQP_URL";

    public void start() {
        String uriString = System.getenv(RABBIT_URI_ENV_NAME);
        if (uriString == null) {
            log.error(RABBIT + "No way to get system environment variable named {}", RABBIT_URI_ENV_NAME);
            return;
        }
        log.info(RABBIT + "URI has been getted from system env {}!", RABBIT_URI_ENV_NAME);

        ConnectionFactory factory = new ConnectionFactory();
        try {
            factory.setUri(uriString);
            factory.setPort(RABBIT_PORT);
        } catch (URISyntaxException | NoSuchAlgorithmException | KeyManagementException e) {
            log.error(RABBIT + "FACTORY.SETURI Errora!", e);
            return;
        }
        log.info(RABBIT + "URI and PORT successfully setted!");
    }
}
