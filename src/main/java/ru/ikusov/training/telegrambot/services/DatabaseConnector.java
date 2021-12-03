package ru.ikusov.training.telegrambot.services;

import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import org.springframework.stereotype.Component;
import ru.ikusov.training.telegrambot.model.Quote;

import javax.annotation.PreDestroy;

@Component
public class DatabaseConnector {
    private final SessionFactory sessionFactory;

    public DatabaseConnector() {
        String connectionUrl = System.getenv("connection_url"),
                userName = System.getenv("connection_username"),
                password = System.getenv("connection_password");


        Configuration configuration = new Configuration();
        configuration.setProperty("hibernate.connection.url", connectionUrl);
        configuration.setProperty("hibernate.connection.username", userName);
        configuration.setProperty("hibernate.connection.password", password);
        configuration.configure("hibernate.cfg.xml");
        configuration.addAnnotatedClass(Quote.class);

        sessionFactory = configuration.buildSessionFactory();

        System.out.println("SessionFactory created!");
    }

    public SessionFactory getSessionFactory() {
        return sessionFactory;
    }

//    @PreDestroy
//    private void closeConnection() {
//        sessionFactory.close();
//    }
}
