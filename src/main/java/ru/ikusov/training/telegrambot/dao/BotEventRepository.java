package ru.ikusov.training.telegrambot.dao;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.ikusov.training.telegrambot.model.BotEventEntity;

@Slf4j
@Component
@RequiredArgsConstructor
public class BotEventRepository {
    private final DatabaseConnector databaseConnector;

    public void save(BotEventEntity botEvent) {
        databaseConnector.save(botEvent);
    }
}
