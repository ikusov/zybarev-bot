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


    public WordleUserPointsDto savePoints(Long chatId, Long userId) {
        List<String> triedWordsForChat = wordleRepository.getTriedWordsForChat(chatId);

        int points = WordlePointsCalculator.countPoints(triedWordsForChat);
        int userSumPoints = wordleRepository.addPointsForUser(chatId, userId, points);

        return new WordleUserPointsDto(points, userSumPoints);
    }
}
