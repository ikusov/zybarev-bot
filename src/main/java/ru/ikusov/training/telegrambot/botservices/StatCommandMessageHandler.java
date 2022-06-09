package ru.ikusov.training.telegrambot.botservices;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import ru.ikusov.training.telegrambot.model.ChatEntity;
import ru.ikusov.training.telegrambot.model.ExampleAnswerEntity;
import ru.ikusov.training.telegrambot.model.MyBotCommand;
import ru.ikusov.training.telegrambot.model.UserEntity;
import ru.ikusov.training.telegrambot.repository.DatabaseConnector;
import ru.ikusov.training.telegrambot.utils.Linguistic;

import java.util.Comparator;
import java.util.List;
import java.util.Set;

@Component
@Order(140)
public class StatCommandMessageHandler extends CommandMessageHandler {
    private final Set<String> commandVariants = Set.of("/stat", "/стат", "/statistics", "/статистика", "/матстат");

    @Autowired
    private DatabaseConnector databaseConnector;

    @Override
    protected Set<String> getCommandVariants() {
        return commandVariants;
    }

    @Override
    protected void addHelp() {
        String help = commandVariants.stream().reduce((s1, s2) -> s1 + ", " + s2).orElse("");
        help += " - статистика по математическим достижениям.\n";
        helpString = help + helpString;
    }


    @Override
    public BotReaction handleCommand(MyBotCommand command) {
        UserEntity user = new UserEntity(command.getUser());
        ChatEntity chat = new ChatEntity(command.getChat());
        String textAnswer = String.format("Статистика для %s в чате %s:", user, chat);


        long userId = command.getUser().getId(),
                chatId = command.getChat().getId();
        List<ExampleAnswerEntity> answers;
        int exampleCount=0, rightCount=0, wrongCount=0, score=0, globalSeries=0, noErrorSeries=0;
        float timeAverage=0f;

        try {
            answers = databaseConnector.getByQuery(ExampleAnswerEntity.class,
                    String.format("from ExampleAnswerEntity"));
            answers.sort(Comparator.reverseOrder());
            boolean noErrorFlag = true, globalSeriesFlag = true;
            for (var answer : answers) {
                if(answer.getChat().getId() == chatId) {
                    if (answer.getUser().getId() != userId) {
                        if (answer.isRight())
                            globalSeriesFlag = false;
                    } else {
                        exampleCount++;
                        timeAverage += answer.getTimer();
                        if (answer.isRight()) {
                            rightCount++;
                            score+=answer.getScore();
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
            wrongCount = exampleCount-rightCount;
            textAnswer += String.format(
                    "\nПопыток решения: %d" +
                    "\nПравильных решений: %d (%.1f%%)" +
                    "\nНеправильных решений: %d (%.1f%%)" +
                    "\nСреднее время решения: %.1f с" +
                    "\nМат. баллов заработано: %d" +
                    "\nГлобальная серия: %d пример%s" +
                    "\nЛичная серия без ошибок: %d пример%s",
                    exampleCount,
                    rightCount, 100.*rightCount/exampleCount,
                    wrongCount, 100.*wrongCount/exampleCount,
                    timeAverage/exampleCount/1000,
                    score,
                    globalSeries, Linguistic.getManulWordEnding(globalSeries),
                    noErrorSeries, Linguistic.getManulWordEnding(noErrorSeries)
            );
        } catch (Exception e) {
            System.out.println("Exception while getting from database: " + e.getMessage());
            textAnswer += " нет данных.";
        }

        return new BotMessageSender(command.getChatId(), textAnswer);
    }
}
