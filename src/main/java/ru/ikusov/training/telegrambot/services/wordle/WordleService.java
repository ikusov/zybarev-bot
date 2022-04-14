package ru.ikusov.training.telegrambot.services.wordle;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import ru.ikusov.training.telegrambot.utils.MyString;

@Component
public class WordleService {
    private String currentWord;
    private GameStatus gameStatus;

    private WordleRepository wordleRepository;

    @Autowired
    public WordleService(WordleRepository wordleRepository) {
        this.wordleRepository = wordleRepository;
        this.gameStatus = GameStatus.NOT_STARTED;
    }

    public void startGame() {
        gameStatus = GameStatus.STARTED;
        currentWord = wordleRepository.getRandomWord();
    }

    public String checkWord(String word) {
        if (!wordIsInDB(word)) {
            return MyString.markdownv2Format("В моём словаре нет слова \"" + word + "\", попробуйте другое!");
        }

        var guessResult = compareWords(word, currentWord);

        return formatToMarkdownV2(word, compareWords(word, currentWord));
    }

    private int[] compareWords(String tested, String right) {
        int[] result = new int[tested.length()];
        char[] t = tested.toCharArray();
        char[] r = right.toCharArray();
        for (int i = 0; i < t.length; i++) {
            result[i] = t[i] == r[i]
                    ? 2
                    : right.indexOf(t[i]) >= 0
                    ? 1
                    : 0;
        }

        return result;
    }

    private String formatToMarkdownV2(String s, int[] wordComparingResult) {
        StringBuilder sb = new StringBuilder(s.length() * 5);
        for (int i = 0; i < s.length(); i++) {
            var c = Character.toUpperCase(s.charAt(i));
            sb.append(
                    wordComparingResult[i] == 2
                            ? "*" + c + "*"
                            : wordComparingResult[i] == 1
                            ? "_" + c + "_"
                            : "~" + c + "~"
            );
        }

        return sb.toString();
    }

    private boolean wordIsInDB(String word) {
        return wordleRepository.isWordInDB(word);
    }

    public enum GameStatus {
        NOT_STARTED,
        STARTED,
        LOCKED
    }
}
