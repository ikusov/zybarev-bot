package ru.ikusov.training.telegrambot.services;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.jsoup.select.Selector;
import ru.ikusov.training.telegrambot.utils.MyString;

import java.io.IOException;
import java.util.NoSuchElementException;

import static ru.ikusov.training.telegrambot.utils.MyMath.r;

public class QuoteGetter {
    private final String url = "https://socratify.net/quotes/random";
    private String text;
    private String markdownv2FormattedText;

    public QuoteGetter() throws IOException {
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
//                else
//                    continue;

                String author = div.select("h2.b-quote__category a")
                                    .html()
                                    .split(",")[0];

                quote = div.select("h1.b-quote__text").text();
                text = quote + "\n" + author;
                markdownv2FormattedText = MyString.markdownv2Format(quote)
                                + "\n_"
                                + MyString.markdownv2Format(author)
                                + "_";

//            //for testing purposes only
//                System.out.printf("%s:%n%s%n",
//                                    suitable ? "Suitable" : "Unsuitable" + " quote:",
//                                    text);
//                System.out.printf("Formatted:\n%s", formattedText);
            }

        } catch (IOException e) {
            throw new IOException("Не могу подключиться к сайту цитаток " + url);
        } catch (Selector.SelectorParseException e) {
            throw new IOException("Невалидный CSS запрос в методе парсинга сайта цитаток " + url);
        }
    }

    public String getQuote() {
        return text;
    }

    public String getMarkdownv2FormattedQuote() {
        return markdownv2FormattedText;
    }
}
