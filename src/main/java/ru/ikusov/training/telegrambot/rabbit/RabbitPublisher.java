package ru.ikusov.training.telegrambot.rabbit;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.net.URISyntaxException;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.util.concurrent.TimeoutException;
import java.util.logging.ErrorManager;

@Component
public class RabbitPublisher {
    private final static String RABBIT = "RABBITRABBITRABBIT ";
    private static final int RABBIT_PORT = 5671;
    private static final String QUEUE_NAME = "ZYBAREV";

    private final Logger log = LoggerFactory.getLogger(this.getClass());
    private final String RABBIT_URI_ENV_NAME = "CLOUDAMQP_URL";

    private ConnectionFactory factory;

    @PostConstruct
    public void init() {
        String uriString = System.getenv(RABBIT_URI_ENV_NAME);
        if (uriString == null) {
            log.error(RABBIT + "No way to get system environment variable named {}", RABBIT_URI_ENV_NAME);
            return;
        }
        log.info(RABBIT + "URI has been getted from system env {}!", RABBIT_URI_ENV_NAME);

        factory = new ConnectionFactory();
        try {
            factory.setUri(uriString);
            factory.setPort(RABBIT_PORT);
        } catch (URISyntaxException | NoSuchAlgorithmException | KeyManagementException e) {
            log.error(RABBIT + "FACTORY.SETURI Errora!", e);
            return;
        }
        log.info(RABBIT + "URI and PORT successfully setted!");
    }

    public void publish(String message) {
        try (Connection connection = factory.newConnection();
             Channel channel = connection.createChannel()) {
            log.info(RABBIT + "Connection succ getted! Channel succ created!");
            channel.queueDeclare(QUEUE_NAME, false, false, false, null);
            channel.basicPublish("", QUEUE_NAME, null, message.getBytes());
            log.info(RABBIT + "Message successfully sent! Message: {}", message);
        } catch (IOException | TimeoutException e) {
            log.error(RABBIT + "Error while publishing a message!", e);
        }
    }
}
