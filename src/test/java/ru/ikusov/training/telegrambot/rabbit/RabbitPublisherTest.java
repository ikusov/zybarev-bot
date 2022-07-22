package ru.ikusov.training.telegrambot.rabbit;

import junit.framework.TestCase;

public class RabbitPublisherTest extends TestCase {
    public void testPublish() {
        RabbitPublisher publisher = new RabbitPublisher();
        publisher.init();
        publisher.publish("Hello!");
    }
}