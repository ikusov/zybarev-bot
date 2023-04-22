package ru.ikusov.training.telegrambot;

import org.hibernate.SessionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import ru.ikusov.training.telegrambot.model.*;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;

@Configuration
@ComponentScan("ru.ikusov.training.telegrambot")
@EnableAspectJAutoProxy
public class MyConfig {
    private final Logger log = LoggerFactory.getLogger(this.getClass());
//    @Bean(name = "sessionFactory")
    public SessionFactory sessionFactory() {
        final List<Class<? extends CommonEntity>> entities =
                List.of(QuoteEntity.class,
                        UserEntity.class,
                        ChatEntity.class,
                        LocationEntity.class,
                        ExampleAnswerEntity.class,
                        WordAttempt.class,
                        WordEntity1.class);

        log.info("Getting database url from system env...");
        String uriString = System.getenv("DATABASE_URL");
        log.info("Database url string was successfully geted! This is the {}", uriString);
        log.info("Trying to construct URI from url string...");
        URI dbUri;
        try {
            dbUri = new URI(uriString);
        } catch (URISyntaxException e) {
            log.error("Unable to construct URI from url string {}", uriString);
            throw new RuntimeException(e);
        }
        log.info("URI was successfully constructed!");

        String username = dbUri.getUserInfo().split(":")[0];
        String password = dbUri.getUserInfo().split(":")[1];
        String connectionUrl = "jdbc:postgresql://" + dbUri.getHost() + ':' + dbUri.getPort() + dbUri.getPath();

        log.info("We have the next connection params now: {}, {}, {}", connectionUrl, username, password);

        log.info("We try to configure hibernate configuration...");
        org.hibernate.cfg.Configuration configuration = new org.hibernate.cfg.Configuration();
        configuration.setProperty("hibernate.connection.url", connectionUrl);
        configuration.setProperty("hibernate.connection.username", username);
        configuration.setProperty("hibernate.connection.password", password);

        configuration.configure("hibernate.cfg.xml");
        log.info("We successfully configured hiber config! We try to add entitites to configuration...");

        entities.stream()
                .peek(e -> log.info("To configuration added entity {}!", e.getSimpleName()))
                .forEach(configuration::addAnnotatedClass);

        log.info("We try to build hibernate session factory...");
        var sf = configuration.buildSessionFactory();
        log.info("Session factory successfully built!");

        return sf;
    }
}
