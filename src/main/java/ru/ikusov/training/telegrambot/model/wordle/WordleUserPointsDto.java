package ru.ikusov.training.telegrambot.model.wordle;

public class WordleUserPointsDto {
    private final int currentPoints;
    private final int sumPoints;
    public WordleUserPointsDto(int currentPoints, int sumPoints) {
        this.currentPoints = currentPoints;
        this.sumPoints = sumPoints;
    }

    public int getCurrentPoints() {
        return currentPoints;
    }

    public int getSumPoints() {
        return sumPoints;
    }
}
