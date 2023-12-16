package ru.ikusov.training.telegrambot.dao;

import jakarta.persistence.Query;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.cfg.Configuration;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Chat;
import org.telegram.telegrambots.meta.api.objects.User;
import ru.ikusov.training.telegrambot.model.*;
import ru.ikusov.training.telegrambot.model.wordle.*;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

@Slf4j
@Component
@RequiredArgsConstructor
public class DatabaseConnector {
    private final SessionFactory sessionFactory;

    private final List<Class<? extends CommonEntity>> entities =
            List.of(
                    BotEventEntity.class,
                    QuoteEntity.class,
                    UserEntity.class,
                    ChatEntity.class,
                    LocationEntity.class,
                    ExampleAnswerEntity.class,
                    WordEntity.class,
                    WordleChatCurrentWordEntity.class,
                    WordleChatWordListEntity.class,
                    WordleChatAttemptListEntity.class,
                    WordleEventEntity.class,
                    WordleUserEntity.class
            );

    public DatabaseConnector() throws URISyntaxException {
        log.info("Getting database url from system env...");
        String uriString = System.getenv("DATABASE_URL");
        log.info("Database url string was successfully geted! This is the {}", uriString);
        log.info("Trying to construct URI from url string...");
        URI dbUri = new URI(System.getenv("DATABASE_URL"));
        log.info("URI was successfully constructed!");

        String username = dbUri.getUserInfo().split(":")[0];
        String password = dbUri.getUserInfo().split(":")[1];
        String connectionUrl = "jdbc:postgresql://" + dbUri.getHost() + ':' + dbUri.getPort() + dbUri.getPath();

        log.info("We have the next connection params now: {}, {}, {}", connectionUrl, username, password);

        Configuration configuration = new Configuration();
        configuration.setProperty("hibernate.connection.url", connectionUrl);
        configuration.setProperty("hibernate.connection.username", username);
        configuration.setProperty("hibernate.connection.password", password);

        configuration.configure("hibernate.cfg.xml");
        entities.forEach(configuration::addAnnotatedClass);

        sessionFactory = configuration.buildSessionFactory();

        //for testing purposes only
        log.info("SessionFactory created!");
    }

    public SessionFactory getSessionFactory() {
        return sessionFactory;
    }

    public void close() {
        sessionFactory.close();
    }

    public <T extends CommonEntity> Optional<T> getById(Class<T> tClass, long id) {
        T entity = null;
        try (Session session = sessionFactory.openSession()) {
            Transaction transaction = session.beginTransaction();

            entity = session.get(tClass, id);

            transaction.commit();
        } catch (Exception e) {
            log.error("Error while getting from DB entity by id '{}'. The mesage is: {}", id, e.getMessage());
        }

        return Optional.ofNullable(entity);
    }

    public <T extends CommonEntity> void save(T entity) {
        try (Session session = sessionFactory.openSession()) {
            Transaction transaction = session.beginTransaction();

            session.save(entity);

            transaction.commit();
        }
    }

    public <T extends CommonEntity> void saveOrUpdate(T entity) {
        try (Session session = sessionFactory.openSession()) {
            Transaction transaction = session.beginTransaction();

            session.saveOrUpdate(entity);

            transaction.commit();
        }
    }

    public <T extends CommonEntity> List<T> getByQueryNotEmpty(Class<T> tClass, String query) {
        List<T> resultList = getByQuery(tClass, query);

        if (resultList == null || resultList.isEmpty()) {
            log.error("No elements for query: {}", query);
            throw new NoSuchElementException("No elements for query " + query);
        }

        return resultList;
    }

    @SuppressWarnings("unchecked")
    public <T extends CommonEntity> List<T> getByQuery(Class<T> tClass, String query) {
        List<T> resultList;

        try (Session session = sessionFactory.openSession()) {
            Transaction transaction = session.beginTransaction();

            Query query1 = session.createQuery(query, tClass);
            resultList = query1.getResultList();

            transaction.commit();
        } catch (Exception e) {
            log.error("Error while execution of query '{}': {}", query, e.getMessage());
            throw e;
        }

        return resultList;
    }

    @SuppressWarnings("unchecked")
    public <T> List<T> getCountByQuery(Class<T> tClass, String query) {
        List<T> resultList;

        try (Session session = sessionFactory.openSession()) {
            Transaction transaction = session.beginTransaction();

            Query query1 = session.createQuery(query, tClass);
            resultList = query1.getResultList();

            transaction.commit();
        } catch (Exception e) {
            log.error("Error while execution of query '{}': {}", query, e.getMessage());
            throw e;
        }

        return resultList;
    }

    public UserEntity getOrCreateUser(User chatUser) {
        UserEntity user;
        var userEntityOptional = getById(UserEntity.class, chatUser.getId());
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
        var chatEntityOptional = getById(ChatEntity.class, telegramChat.getId());
        if (chatEntityOptional.isEmpty()) {
            chat = new ChatEntity(telegramChat);

            save(chat);
        } else {
            chat = chatEntityOptional.get();
        }

        return chat;
    }


}
