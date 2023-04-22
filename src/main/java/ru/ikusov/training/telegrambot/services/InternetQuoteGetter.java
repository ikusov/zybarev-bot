package ru.ikusov.training.telegrambot.services;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.jsoup.select.Selector;
import ru.ikusov.training.telegrambot.utils.MyString;

import java.io.IOException;
import java.util.NoSuchElementException;

public class InternetQuoteGetter implements QuoteGetter, Markdownv2QuoteGetter {
    private final String url = "https://socratify.net/quotes/random";
    private String text;
    private String markdownv2FormattedText;

    public InternetQuoteGetter() throws IOException {
        Document document;
        boolean suitable = false;
        try {
            while (!suitable) {
                document = Jsoup.connect(url).get();
                Elements elements = document.select("div.b-quote");
                if (elements.isEmpty())
                    throw new NoSuchElementException("Не найден контент с цитатками на сайте цитаток " + url);

                Element div = elements.get(0);
                String quote = div.select("h1.b-quote__text").html();
                if (!quote.contains("br"))
                    suitable = true;

                String author = div.select("h2.b-quote__category a")
                        .html()
                        .split(",")[0];

                quote = div.select("h1.b-quote__text").text();
                text = quote + "\n" + author;
                markdownv2FormattedText = MyString.markdownv2Format(quote)
                        + "\n_"
                        + MyString.markdownv2Format(author)
                        + "_";
            }

        } catch (IOException e) {
            throw new IOException("Не могу подключиться к сайту цитаток " + url);
        } catch (Selector.SelectorParseException e) {
            throw new IOException("Невалидный CSS запрос в методе парсинга сайта цитаток " + url);
        }
    }

    @Override
    public String getQuote(int... args) {
        return text;
    }

    @Override
    public String getMarkdownv2FormattedQuote(String... args) {
        return markdownv2FormattedText;
    }
}
