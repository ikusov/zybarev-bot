package ru.ikusov.training.telegrambot.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.ikusov.training.telegrambot.model.ExampleAnswerEntity;

public interface ExampleRepository extends JpaRepository<ExampleAnswerEntity, Long> {
}
