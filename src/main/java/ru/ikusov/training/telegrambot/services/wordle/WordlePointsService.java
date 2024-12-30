package ru.ikusov.training.telegrambot.services.wordle;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import ru.ikusov.training.telegrambot.dao.wordle.WordleRepository;
import ru.ikusov.training.telegrambot.model.wordle.WordleUserPointsDto;

import java.util.List;

@Component
public class WordlePointsService {
    private final WordleRepository wordleRepository;

    @Autowired
    public WordlePointsService(WordleRepository wordleRepository) {
        this.wordleRepository = wordleRepository;
    }

    public WordleUserPointsDto earnPointsForUnguessed(Long chatId, Long userId) {
        List<String> triedWordsForChat = wordleRepository.getTriedWordsForChat(chatId);
        int lastIndex = triedWordsForChat.size() - 1;

        String rightWord = wordleRepository.getCurrentWordForChat(chatId);
        String currentAttemptWord = triedWordsForChat.get(lastIndex);
        List<String> triedWords = triedWordsForChat.subList(0, lastIndex);

        int points = WordlePointsCalculator.countPointsForUnGuessed(rightWord, currentAttemptWord, triedWords);
        int userSumPoints = wordleRepository.addPointsForUser(chatId, userId, points);

        return new WordleUserPointsDto(points, userSumPoints);
    }

    public WordleUserPointsDto earnPointsForGuessed(Long chatId, Long userId) {
        List<String> triedWordsForChat = wordleRepository.getTriedWordsForChat(chatId);
        int lastIndex = triedWordsForChat.size() - 1;

        String rightWord = triedWordsForChat.get(lastIndex);
        List<String> triedWords = triedWordsForChat.subList(0, lastIndex);

        int points = WordlePointsCalculator.countPointsForGuessed(rightWord, rightWord, triedWords);
        int userSumPoints = wordleRepository.addPointsForUser(chatId, userId, points);

        return new WordleUserPointsDto(points, userSumPoints);
    }
}
