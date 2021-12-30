package ru.ikusov.training.telegrambot.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import ru.ikusov.training.telegrambot.model.ExampleAnswerEntity;
import ru.ikusov.training.telegrambot.model.UserEntity;

import java.util.*;

@Component
public class MathTopGetter {
    @Autowired
    DatabaseConnector databaseConnector;

    /**
     * get descending sorted list of users and their points
     * @param chatId id of chat where to count score
     * @return list of users with their score
     */
    public List<Map.Entry<UserEntity, Long>> getMathTop(String chatId) {
        HashMap<UserEntity, Long> userScoreMap = new HashMap<>();
        List<ExampleAnswerEntity> answers =
                databaseConnector.getByQuery(ExampleAnswerEntity.class,
                        "from ExampleAnswerEntity where is_right=true and chat_id=" + chatId);

        for (ExampleAnswerEntity answer : answers) {
            UserEntity user = answer.getUser();
            Long count = userScoreMap.putIfAbsent(user, (long) answer.getScore());
            if (count != null)
                userScoreMap.put(user, count+answer.getScore());
        }

        List<Map.Entry<UserEntity, Long>> userScoreList =
                            new ArrayList<>(userScoreMap.entrySet());

        //sort by score descending order
        Comparator<Map.Entry<UserEntity, Long>> myComp =
                (e1, e2) -> Long.compare(e2.getValue(), e1.getValue());
        userScoreList.sort(myComp);

        return userScoreList;
    }
}
