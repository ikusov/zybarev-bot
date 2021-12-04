package ru.ikusov.training.telegrambot.services;

import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import org.springframework.stereotype.Component;
import ru.ikusov.training.telegrambot.model.Quote;

import javax.annotation.PreDestroy;
import java.net.URI;
import java.net.URISyntaxException;

@Component
public class DatabaseConnector {
    private final SessionFactory sessionFactory;

    public DatabaseConnector() throws URISyntaxException {
        URI dbUri = new URI(System.getenv("DATABASE_URL"));

        String username = dbUri.getUserInfo().split(":")[0];
        String password = dbUri.getUserInfo().split(":")[1];
        String connectionUrl = "jdbc:postgresql://" + dbUri.getHost() + ':' + dbUri.getPort() + dbUri.getPath();

//        String connectionUrl = System.getenv("JDBC_DATABASE_URL");

        Configuration configuration = new Configuration();
        configuration.setProperty("hibernate.connection.url", connectionUrl);
        configuration.setProperty("hibernate.connection.username", username);
        configuration.setProperty("hibernate.connection.password", password);

        configuration.configure("hibernate.cfg.xml");
        configuration.addAnnotatedClass(Quote.class);

        sessionFactory = configuration.buildSessionFactory();

        //for testing purposes only
        System.out.println("SessionFactory created!");
    }

    public SessionFactory getSessionFactory() {
        return sessionFactory;
    }

    public void close() {
        sessionFactory.close();
    }

//    @PreDestroy
//    private void closeConnection() {
//        sessionFactory.close();
//    }
}
