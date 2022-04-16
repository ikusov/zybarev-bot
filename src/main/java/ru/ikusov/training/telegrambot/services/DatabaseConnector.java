package ru.ikusov.training.telegrambot.services;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.cfg.Configuration;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Chat;
import org.telegram.telegrambots.meta.api.objects.User;
import ru.ikusov.training.telegrambot.model.*;

import javax.persistence.Query;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

@Component
public class DatabaseConnector {
    private final SessionFactory sessionFactory;
    private final List<Class<? extends CommonEntity>> entities =
            List.of(QuoteEntity.class,
                    UserEntity.class,
                    ChatEntity.class,
                    LocationEntity.class,
                    ExampleAnswerEntity.class,
                    WordEntity.class);

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

    public <T extends CommonEntity> Optional<T> getById(Class<T> tClass, long id) {
        T entity = null;
        try(Session session = sessionFactory.openSession()) {
            Transaction transaction = session.beginTransaction();

            entity = session.get(tClass, id);

            transaction.commit();
        } catch (Exception e) {
            System.err.println(e.getMessage());
        }

        return Optional.ofNullable(entity);
    }

    public <T extends CommonEntity> void save(T entity) {
        try(Session session = sessionFactory.openSession()) {
            Transaction transaction = session.beginTransaction();

            session.save(entity);

            transaction.commit();
        }
    }

    public <T extends CommonEntity> void saveOrUpdate(T entity) {
        try(Session session = sessionFactory.openSession()) {
            Transaction transaction = session.beginTransaction();

            session.saveOrUpdate(entity);

            transaction.commit();
        }
    }

    public <T extends CommonEntity> List<T> getByQuery(Class<T> tClass, String query) {
        List<T> resultList = null;

        try(Session session = sessionFactory.openSession()) {
            Transaction transaction = session.beginTransaction();

            Query query1 = session.createQuery(query, tClass);
            resultList = query1.getResultList();

            transaction.commit();
        } catch (Exception e) {
            System.err.println(e.getMessage());
        }

        if (resultList == null || resultList.isEmpty()) throw new NoSuchElementException("No elements for query " + query);

        return resultList;
    }

    public UserEntity getOrCreateUser(User chatUser) {
        UserEntity user;
        var userEntityOptional= getById(UserEntity.class, chatUser.getId());
        if (userEntityOptional.isEmpty()) {
            user = new UserEntity(chatUser);

            save(user);
        } else {
            user = userEntityOptional.get();
        }

        return user;
    }

    public ChatEntity getOrCreateChat(Chat telegramChat) {
        ChatEntity chat;
        var chatEntityOptional= getById(ChatEntity.class, telegramChat.getId());
        if (chatEntityOptional.isEmpty()) {
            chat = new ChatEntity(telegramChat);

            save(chat);
        } else {
            chat = chatEntityOptional.get();
        }

        return chat;
    }


}
