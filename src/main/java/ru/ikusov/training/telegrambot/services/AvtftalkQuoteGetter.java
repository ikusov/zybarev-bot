package ru.ikusov.training.telegrambot.services;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import ru.ikusov.training.telegrambot.model.QuoteEntity;
import ru.ikusov.training.telegrambot.repository.DatabaseConnector;
import ru.ikusov.training.telegrambot.utils.MyString;

import javax.persistence.Query;
import java.util.List;
import java.util.NoSuchElementException;

import static ru.ikusov.training.telegrambot.utils.MyMath.r;

@Component
public class AvtftalkQuoteGetter implements Markdownv2QuoteGetter {
    @Autowired
    private DatabaseConnector databaseConnector;

    @Override
    public String getMarkdownv2FormattedQuote(String... args) {
        SessionFactory sessionFactory = databaseConnector.getSessionFactory();
        String result;
        int number;

        try {
            if(args.length==0 || args[0].equals("")) {
                number = r(72) + 1;
            } else {
                number = MyString.brutalParseInt(args[0]);
            }
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Не могу понять, что за номер цитаты " + args[0]);
        }

        try (Session session = sessionFactory.getCurrentSession()) {

            Transaction transaction = session.beginTransaction();

            Query query = session.createQuery("from QuoteEntity where channel='#avtftalk' and id=" + number);
            List<QuoteEntity> quotes = query.getResultList();

            if (quotes.isEmpty()) {
                throw new NoSuchElementException("Не найдена цитата за нумером " + number + "!");
            }
            QuoteEntity quote = quotes.get(0);
            result = quote.toMarkdownv2String();

            transaction.commit();

        } catch (NoSuchElementException e) {
            result = MyString.markdownv2Format(e.getMessage());
        }

        return result;
    }
}
