package ru.ikusov.training.telegrambot.services;

import org.jsoup.Jsoup;
import org.jsoup.select.Selector;
import ru.ikusov.training.telegrambot.utils.MessageType;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

import static ru.ikusov.training.telegrambot.utils.MyMath.r;

public class ImageGetter {
    private final String URL = "https://yandex.ru/images/search?text=";
    private String url;
    private List<String> imagesList;

    public ImageGetter(String searchText) throws IOException {
        String textToFind = searchText == null || searchText.isEmpty() ?
//                RandomMessageGenerator.generate(RandomMessageGenerator.MessageType.PAINTING_QUERY)
                MessageType.PAINTING_QUERY.getRandomMessage() :
                searchText;

        textToFind = textToFind.replaceAll("\\s", "%20");

        url = URL + textToFind;

        try {
            imagesList = Jsoup.connect(url + textToFind)
                    .get().select("div.serp-controller__content img").stream()
                    .map(img -> img.attr("abs:src"))
                    .collect(Collectors.toList());
        } catch (IOException e) {
            throw new IOException("Не могу подключиться к сайту " + url);
        } catch (Selector.SelectorParseException e) {
            throw new IOException("Невалидный CSS запрос в методе парсинга сайта " + url);
        }
    }

    public String getImage() {
        logImageGetting();
        if (imagesList != null && !imagesList.isEmpty()) {
            int index = r(imagesList.size());
            return imagesList.get(index);
        } else {
            throw new NoSuchElementException("Изображения почему-то не найдены по запросу " + url);
        }
    }

    private void logImageGetting() {
        System.out.printf("%s: get %s images by request %s%n",
                LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")),
                imagesList == null ? "null" : String.valueOf(imagesList.size()),
                url);
    }
}
