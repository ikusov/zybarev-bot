package ru.ikusov.training.telegrambot.services.wordle;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.Chat;
import org.telegram.telegrambots.meta.api.objects.User;
import ru.ikusov.training.telegrambot.dao.DatabaseConnector;
import ru.ikusov.training.telegrambot.dao.wordle.WordleEventRepository;
import ru.ikusov.training.telegrambot.dao.wordle.WordleRepository;
import ru.ikusov.training.telegrambot.model.UserEntity;
import ru.ikusov.training.telegrambot.model.wordle.WordleEventEntity;
import ru.ikusov.training.telegrambot.model.wordle.WordleUserEntity;
import ru.ikusov.training.telegrambot.services.UserNameGetter;
import ru.ikusov.training.telegrambot.utils.MyString;

import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import static ru.ikusov.training.telegrambot.utils.Linguistic.getPointWordEndingIme;

@Service
public class WordleStatService {
    private final WordleEventRepository wordleEventRepository;
    private final WordleRepository wordleRepository;
    private final DatabaseConnector databaseConnector;

    @Autowired
    public WordleStatService(WordleEventRepository wordleEventRepository, WordleRepository wordleRepository, DatabaseConnector databaseConnector) {
        this.wordleEventRepository = wordleEventRepository;
        this.wordleRepository = wordleRepository;
        this.databaseConnector = databaseConnector;
    }

    /**
     * Returns Markdown M2 formatted wordle statistics for chat and user
     * @param chat chat
     * @param user user
     * @return statistics
     */
    public String getStat(Chat chat, User user) {
        var oldestEventTimestamp = wordleEventRepository.getOldestEventTimestamp();
        String userStatString = "";
        var userName = UserNameGetter.getUserName(user);
        //Статистика для пользователя /Слепой Пью/:
        userStatString += "*" + m("Статистика для пользователя ")
                + m(userName) + "*" + m(":") + "\n";

        var sumPointsForUser = wordleRepository.getPointsForUserInChat(chat.getId(), user.getId());
        userStatString += m("Очков: ") + "*" + sumPointsForUser + "*\n";

        var eventsForUser = wordleEventRepository.getEventsForUser(user.getId(), chat.getId());
        userStatString += m("Слов загадано: " + countMadeWords(eventsForUser)) + "\n";
        userStatString += m("Попыток отгадки: " + countAttempts(eventsForUser)) + "\n";
        userStatString += m("Слов отгадано: " + countGuessed(eventsForUser)) + "\n\n";

        var eventsForChat =
                wordleEventRepository.getEventsForChat(chat.getId());
        var chatStatString = "*" + m("Статистика для чата: ") + "*\n";
        chatStatString += m("Слов загадано: " + countMadeWords(eventsForChat)) + "\n";
        chatStatString += m("Попыток отгадки: " + countAttempts(eventsForUser)) + "\n\n";

        Long countMadeWords = wordleEventRepository.countMadeWords();
        Long countAttempts = wordleEventRepository.countAttempts();
        double dailyMadeWordsRate =
                (double) countMadeWords * (double) (24 * 3600)
                        / ((double) Instant.now().getEpochSecond() - (double) oldestEventTimestamp);
        var dailyMadeWordsString = String.format("%.1f", dailyMadeWordsRate);
        var allStatString = "*" + m("Общая статистика: ") + "*\n";
        allStatString += m("Слов загадано: " + countMadeWords) + "\n";
        allStatString += m("Попыток отгадки: " + countAttempts) + "\n";
        allStatString += m("В среднем за сутки загаданных слов: " + dailyMadeWordsString + " шт.") + "\n\n";

        var instant = Instant.ofEpochSecond(oldestEventTimestamp);
        var dateTime = DateTimeFormatter.ISO_LOCAL_DATE.format(instant.atZone(ZoneId.systemDefault()));
        var infoMsg = "_" + m("(статистика учитывается с " + dateTime + ")") + "_\r";

        return userStatString + chatStatString + allStatString + infoMsg;
    }

    public List<String> getTop(Chat chat, int topNumber) {
        List<WordleUserEntity> wueList = wordleRepository.getWordleUsersForChat(chat.getId());
        wueList.sort(Comparator.comparing(WordleUserEntity::getPoints).reversed());

        List<String> topList = new ArrayList<>();
        for (int i = 0; i < Math.min(wueList.size(), topNumber); i++) {
            var wue = wueList.get(i);
            var userId = wue.getUserId();
            var userEntity = databaseConnector.getById(UserEntity.class, userId).orElse(null);
            String userName = userEntity == null ? "" + userId : UserNameGetter.getUserName(userEntity);

            var points = wue.getPoints();

            var place = i + 1;
            String userString = place + ". " + userName + ": " + points + " очк" + getPointWordEndingIme(points.intValue());
            topList.add(userString);
        }

        return topList;
    }

    private long countGuessed(List<WordleEventEntity> events) {
        return events.stream()
                .filter(WordleEventEntity::isRight)
                .count();
    }

    private long countAttempts(List<WordleEventEntity> events) {
        return events.stream()
                .filter(e -> e.getAttemptWord() != null)
                .count();
    }

    private long countMadeWords(List<WordleEventEntity> events) {
        return events.stream()
                .filter(e -> e.getAttemptWord() == null)
                .count();
    }

    private String m(String toM2) {
        return MyString.markdownv2Format(toM2);
    }
}
