package ru.ikusov.training.telegrambot.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import ru.ikusov.training.telegrambot.utils.MyString;

import java.text.SimpleDateFormat;
import java.util.Date;

@Entity
@Table(name = "quotes")
public class QuoteEntity implements CommonEntity {
    @Id
    @Column(name = "id")
    private int id;

    @Column(name = "deleted")
    private int deleted;

    @Column(name = "author")
    private String author;

    @Column(name = "quote")
    private String quote;

    @Column(name = "channel")
    private String channel;

    @Column(name = "time")
    private int time;

    public QuoteEntity() {
    }

    public int getId() {
        return id;
    }

    public QuoteEntity setId(int id) {
        this.id = id;
        return this;
    }

    public int getDeleted() {
        return deleted;
    }

    public QuoteEntity setDeleted(int deleted) {
        this.deleted = deleted;
        return this;
    }

    public String getAuthor() {
        return author;
    }

    public QuoteEntity setAuthor(String author) {
        this.author = author;
        return this;
    }

    public String getQuote() {
        return quote;
    }

    public QuoteEntity setQuote(String quote) {
        this.quote = quote;
        return this;
    }

    public String getChannel() {
        return channel;
    }

    public QuoteEntity setChannel(String channel) {
        this.channel = channel;
        return this;
    }

    public int getTime() {
        return time;
    }

    public QuoteEntity setTime(int time) {
        this.time = time;
        return this;
    }

    @Override
    public String toString() {
        Date date = new Date(time*1000L);
        String dateTime = new SimpleDateFormat("yyyy-MM-dd hh:mm").format(date);
        return String.format("%s %s %s", dateTime, author, quote);
    }

    public String toMarkdownv2String() {
        Date date = new Date(time*1000L);
        String dateTime = new SimpleDateFormat("yyyy-MM-dd hh:mm").format(date);

        return "_" + MyString.markdownv2Format(dateTime) + "_ " +
               "*" + MyString.markdownv2Format(author) + "* " +
               MyString.markdownv2Format(quote);
    }
}
