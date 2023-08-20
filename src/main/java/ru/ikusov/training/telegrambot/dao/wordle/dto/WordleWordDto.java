package ru.ikusov.training.telegrambot.dao.wordle.dto;

/**
 * DTO for made word for chat
 *
 * @param word made word for chat
 * @param wordsCount count of words last for making for chat
 */
public record WordleWordDto(
        String word,
        Integer wordsCount
) {
}
