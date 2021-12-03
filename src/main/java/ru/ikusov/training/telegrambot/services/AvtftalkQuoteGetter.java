package ru.ikusov.training.telegrambot.services;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.cfg.Configuration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import ru.ikusov.training.telegrambot.model.Quote;
import ru.ikusov.training.telegrambot.utils.MyString;

import javax.annotation.PreDestroy;
import javax.persistence.Query;
import java.util.List;
import java.util.NoSuchElementException;

@Component
public class AvtftalkQuoteGetter {
    @Autowired
    private DatabaseConnector databaseConnector;

    public String getMarkdownv2FormattedQuote(int number) {
        SessionFactory sessionFactory = databaseConnector.getSessionFactory();
        String result;

        try (Session session = sessionFactory.getCurrentSession()) {

            Transaction transaction = session.beginTransaction();

            Query query = session.createQuery("from Quote where channel='#avtftalk' and id=" + number);
            List<Quote> quotes = query.getResultList();

            if (quotes.isEmpty()) {
                throw new NoSuchElementException("Не найдена цитата за нумером " + number + "!");
            }
            Quote quote = quotes.get(0);
            result = quote.toMarkdownv2String();

            transaction.commit();

        } catch (NoSuchElementException e) {
            result = MyString.markdownv2Format(e.getMessage());
        }

        return result;
    }

}
