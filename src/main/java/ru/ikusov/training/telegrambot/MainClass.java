package ru.ikusov.training.telegrambot;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;

import java.util.Locale;
import java.util.Optional;

public class MainClass {
    public static final Logger log = LoggerFactory.getLogger(MainClass.class);
    public static final String AVTFTALK_CHAT_ID = Optional.ofNullable((System.getenv("avtftalk_chat_id"))).orElse("");
    public static final String WEATHER_API_KEY = Optional.ofNullable((System.getenv("weather_api_token"))).orElse("");
    public static final String GEOCODE_API_KEY = Optional.ofNullable((System.getenv("geocode_api_key"))).orElse("");
    public static final Locale RUS_LOCALE = new Locale("ru", "RU");

    public static final boolean IS_TEST_MODE = false;
//    public static final Long TEST_CHAT_ID = -467690956L;
    public static final Long TEST_CHAT_ID = 349513007L;

    public static void main(String... args) {

        try (var context = new AnnotationConfigApplicationContext(MyConfig.class)) {
            log.info("Trying to get bot bean from {}...", context.getClass().getSimpleName());
            var myBot = context.getBean("bot", Bot.class);
            log.info("Bot bean successfully getted! Trying to create new TelegramBotsApi with DefaultBotSession...");
            var botsApi = new TelegramBotsApi(DefaultBotSession.class);
            log.info("TelegramBotsApi successfully created! Trying to register the bot in the api...");
            botsApi.registerBot(myBot);
            log.info("The Bot has been successfully registered!");
        } catch (Exception e) {
            log.error("General Error!", e);
        }
    }

}
