package ru.ikusov.training.telegrambot;

import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;

public class MainClass {

    public static void main(String... lksdjf) {

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
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
