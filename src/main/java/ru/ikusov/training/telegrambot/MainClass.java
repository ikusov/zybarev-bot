package ru.ikusov.training.telegrambot;

import org.springframework.beans.BeansException;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;

import java.io.IOException;

public class MainClass {

    public static void main(String... lksdjf) {

        AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(MyConfig.class);
//        //for proxy if needed (no)
//        System.getProperties().put( "proxySet", "true" );
//        System.getProperties().put( "socksProxyHost", "127.0.0.1" );
//        System.getProperties().put( "socksProxyPort", "9150" );

        TelegramBotsApi botsApi;
        Bot myBot;
        try {
            myBot = context.getBean("bot", Bot.class);

            botsApi = new TelegramBotsApi(DefaultBotSession.class);
            botsApi.registerBot(myBot);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            context.close();
        }
    }

}
