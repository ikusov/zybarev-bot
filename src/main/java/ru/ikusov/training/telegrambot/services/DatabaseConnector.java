package ru.ikusov.training.telegrambot.services;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.cfg.Configuration;
import org.springframework.stereotype.Component;
import ru.ikusov.training.telegrambot.model.CommonEntity;
import ru.ikusov.training.telegrambot.model.ExampleAnswerEntity;
import ru.ikusov.training.telegrambot.model.QuoteEntity;
import ru.ikusov.training.telegrambot.model.UserEntity;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;

@Component
public class DatabaseConnector {
    private final SessionFactory sessionFactory;
    private final List<Class<? extends CommonEntity>> entities =
            List.of(QuoteEntity.class, UserEntity.class, ExampleAnswerEntity.class);

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
        entities.forEach(configuration::addAnnotatedClass);

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

    public <T extends CommonEntity> T getById(Class<T> tClass, long id) {
        T entity;
        try(Session session = sessionFactory.openSession()) {
            Transaction transaction = session.beginTransaction();

            entity = session.get(tClass, id);

            transaction.commit();
        }

        return entity;
    }

    public <T extends CommonEntity> void save(T entity) {
        try(Session session = sessionFactory.openSession()) {
            Transaction transaction = session.beginTransaction();

            session.save(entity);

            transaction.commit();
        }
    }
}
