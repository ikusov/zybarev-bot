package ru.ikusov.training.telegrambot;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;
import ru.ikusov.training.telegrambot.rabbit.RabbitPublisher;

import java.util.Locale;
import java.util.Optional;

public class MainClass {
    public static final Logger log = LoggerFactory.getLogger(MainClass.class);
    public static final String AVTFTALK_CHAT_ID = Optional.ofNullable((System.getenv("avtftalk_chat_id"))).orElse("");
    public static final String WEATHER_API_KEY = Optional.ofNullable((System.getenv("weather_api_token"))).orElse("");
    public static final String GEOCODE_API_KEY = Optional.ofNullable((System.getenv("geocode_api_key"))).orElse("");
    public static final Locale RUS_LOCALE = new Locale("ru", "RU");

    public static void main(String... nomatterhowtocallthisargument) {

        //        //for proxy if needed (no)
//        System.getProperties().put( "proxySet", "true" );
//        System.getProperties().put( "socksProxyHost", "127.0.0.1" );
//        System.getProperties().put( "socksProxyPort", "9150" );

        try (AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(MyConfig.class)) {
            TelegramBotsApi botsApi;
            Bot myBot;
            myBot = context.getBean("bot", Bot.class);

            botsApi = new TelegramBotsApi(DefaultBotSession.class);
            botsApi.registerBot(myBot);

            log.info("Bot successfully registered.");
//            new RabbitPublisher().start();
        } catch (Exception e) {
            log.error("General Error: {}! Message was: {}", e.getClass(), e.getMessage());
            e.printStackTrace();
        }
    }

}
