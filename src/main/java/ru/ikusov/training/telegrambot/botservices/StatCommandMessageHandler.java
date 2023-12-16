package ru.ikusov.training.telegrambot.botservices;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.ikusov.training.telegrambot.dao.DatabaseConnector;
import ru.ikusov.training.telegrambot.model.*;
import ru.ikusov.training.telegrambot.utils.Linguistic;

import java.util.Comparator;
import java.util.List;

//TODO: этому лапшекоду нужен рефакторинг
@Component
@RequiredArgsConstructor
public class StatCommandMessageHandler extends CommandMessageHandler {

    private final DatabaseConnector databaseConnector;

    @Override
    protected CommandType getSupportedCommandType() {
        return CommandType.STAT;
    }

    @Override
    public BotReaction handleCommand(MyBotCommand command) {
        UserEntity user = new UserEntity(command.user());
        ChatEntity chat = new ChatEntity(command.chat());
        String textAnswer = String.format("Статистика для %s в чате %s:", user, chat);


        long userId = command.user().getId(),
                chatId = command.chat().getId();
        List<ExampleAnswerEntity> answers;
        int exampleCount = 0, rightCount = 0, wrongCount, score = 0, globalSeries = 0, noErrorSeries = 0;
        float timeAverage = 0f;

        try {
            answers = databaseConnector.getByQueryNotEmpty(ExampleAnswerEntity.class,
                    "from ExampleAnswerEntity");
            answers.sort(Comparator.reverseOrder());
            boolean noErrorFlag = true, globalSeriesFlag = true;
            for (var answer : answers) {
                if (answer.getChat().getId() == chatId) {
                    if (answer.getUser().getId() != userId) {
                        if (answer.isRight())
                            globalSeriesFlag = false;
                    } else {
                        exampleCount++;
                        timeAverage += answer.getTimer();
                        if (answer.isRight()) {
                            rightCount++;
                            score += answer.getScore();
                            if (globalSeriesFlag)
                                globalSeries++;
                            if (noErrorFlag)
                                noErrorSeries++;
                        } else {
                            noErrorFlag = false;
                        }
                    }
                }
            }
            wrongCount = exampleCount - rightCount;
            textAnswer += String.format(
                    """

                            Попыток решения: %d
                            Правильных решений: %d (%.1f%%)
                            Неправильных решений: %d (%.1f%%)
                            Среднее время решения: %.1f с
                            Мат. баллов заработано: %d
                            Глобальная серия: %d пример%s
                            Личная серия без ошибок: %d пример%s""",
                    exampleCount,
                    rightCount, 100. * rightCount / exampleCount,
                    wrongCount, 100. * wrongCount / exampleCount,
                    timeAverage / exampleCount / 1000,
                    score,
                    globalSeries, Linguistic.getManulWordEnding(globalSeries),
                    noErrorSeries, Linguistic.getManulWordEnding(noErrorSeries)
            );
        } catch (Exception e) {
            System.out.println("Exception while getting from database: " + e.getMessage());
            textAnswer += " нет данных.";
        }

        return new BotMessageSender(command.chatId(), command.topicId(), textAnswer);
    }
}
