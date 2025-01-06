package ru.ikusov.training.telegrambot.utitilies;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.ikusov.training.telegrambot.dao.DatabaseConnector;
import ru.ikusov.training.telegrambot.model.wordle.WordEntity;
import ru.ikusov.training.telegrambot.services.wordle.WiktionaryWordChecker;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

import static ru.ikusov.training.telegrambot.TestUtils.delayRandomTime;

/**
 * Набор временных утилит разработчика для манипуляций на БД, проверок и т.п.
 */

public class Utilities {
    public static final String ANSI_RESET = "\u001B[0m";
    public static final String ANSI_RED = "\u001B[31m";
    public static final String ANSI_GREEN = "\u001B[32m";
    public static final String ANSI_YELLOW = "\u001B[33m";

    private DatabaseConnector databaseConnector;
    private WiktionaryWordChecker wordChecker;


    @BeforeEach
    void setUp() throws URISyntaxException {
        databaseConnector = new DatabaseConnector();
        wordChecker = new WiktionaryWordChecker();
    }

    @Test
    void checkWords() {
        var wordEntityList = databaseConnector.getByQuery(WordEntity.class, "from WordEntity");
        System.out.printf("%n%nВыбрано %d записей из таблицы wordle_word%n", wordEntityList.size());
        System.out.println("Проверяю слова в Wiktionary...");

        int count = 0;
        List<String> unsuitableWords = new ArrayList<>();
        long startTime = System.currentTimeMillis();

        for (int i = 0; i < wordEntityList.size(); i++) {
            var wordEntity = wordEntityList.get(i);
            var word = wordEntity.getWord();
            String color = ANSI_GREEN;

            delayRandomTime(1000, 5000);
            try {
                boolean result = wordChecker.check(word);
                if (!result) {
                    color = ANSI_YELLOW;
                    unsuitableWords.add(word);
                }
            } catch (IOException e) {
                color = ANSI_RED;
            }

            count++;
            print(word, color, count, System.currentTimeMillis() - startTime, unsuitableWords);
        }

        System.out.println("\nСписок слов, которые не прочекались в Wiktionary:\n" + unsuitableWords);
    }

    void print(String word, String color, int count, long spentMillis, List<String> unsuitableWords) {
        System.out.print(colorString(word, color) + " ");

        if (count % 10 == 0) {
            System.out.println();
        }

        long spentSeconds = spentMillis / 1000;
        if (spentMillis / 1000 % 60 == 0) {
            System.out.printf("%nЗа %d минут проверено %d слов!%n%n", spentSeconds / 60, count);
            System.out.printf("%nНайдено %d непрочеканных:%n%s%n%n", unsuitableWords.size(), unsuitableWords);
        }

        System.out.flush();
    }

    String colorString(String source, String color) {
        return color + source + ANSI_RESET;
    }
}
