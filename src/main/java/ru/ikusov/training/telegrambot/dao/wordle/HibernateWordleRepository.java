package ru.ikusov.training.telegrambot.dao.wordle;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;
import ru.ikusov.training.telegrambot.dao.DatabaseConnector;
import ru.ikusov.training.telegrambot.dao.wordle.dto.WordleWordDto;
import ru.ikusov.training.telegrambot.model.WordleEventDto;
import ru.ikusov.training.telegrambot.model.wordle.*;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.IntStream;

import static ru.ikusov.training.telegrambot.utils.CommonUtils.isNullOrEmpty;
import static ru.ikusov.training.telegrambot.utils.MyString.takeFromList;

@Component
@Primary
public class HibernateWordleRepository implements WordleRepository {
    private final Logger log = LoggerFactory.getLogger(this.getClass());
    private final DatabaseConnector databaseConnector;

    @Autowired
    public HibernateWordleRepository(DatabaseConnector databaseConnector) {
        this.databaseConnector = databaseConnector;
    }

    @Override
    public String getCurrentWordForChat(Long chatId) {
        var chatCurrentWordEntity = databaseConnector.getById(WordleChatCurrentWordEntity.class, chatId);
        if (chatCurrentWordEntity.isEmpty()) {
            return "";
        }
        var currentWord = chatCurrentWordEntity.get().getCurrentWord();
        return currentWord != null
                ? currentWord
                : "";
    }

    @Override
    public List<String> getTriedWordsForChat(Long chatId) {
        var chatAttemptListOptional = databaseConnector.getById(WordleChatAttemptListEntity.class, chatId);
        if (chatAttemptListOptional.isEmpty()) {
            return List.of();
        }
        var attemptList = chatAttemptListOptional.get().getAttemptList();
        return attemptList != null
                ? attemptList
                : List.of();
    }

    @Override
    public boolean isAnyWordAttempts(Long chatId) {
        return !getTriedWordsForChat(chatId).isEmpty();
    }

    @Override
    public int getWordAttemptsCount(Long userId, Long chatId, int currentAllowedAttempts) {
        String hql = getHQL(userId, chatId);
        var now = Instant.now().getEpochSecond();
        WordleUserEntity wordleUserEntity = new WordleUserEntity(userId, chatId, currentAllowedAttempts, now);

        var wueList = databaseConnector.getByQuery(WordleUserEntity.class, hql);
        if (wueList.isEmpty()) {
            databaseConnector.save(wordleUserEntity);
            return currentAllowedAttempts;
        }

        wordleUserEntity = wueList.get(0);
        long timeElapsed = now - wordleUserEntity.getFirstAttemptTimestampSeconds();

        if (timeElapsed >= TIME_EXPIRING_WORD_ATTEMPT_SECONDS) {
            wordleUserEntity.setAttemptsCount(currentAllowedAttempts);
            wordleUserEntity.setFirstAttemptTimestampSeconds(now);
            databaseConnector.saveOrUpdate(wordleUserEntity);
            return currentAllowedAttempts;
        }

        return wordleUserEntity.getAttemptsCount();
    }

    @Override
    public long getWordAttemptTTL(Long userId, Long chatId) {
        String hql = getHQL(userId, chatId);
        var now = Instant.now().getEpochSecond();
        var wueList = databaseConnector.getByQuery(WordleUserEntity.class, hql);
        return wueList.isEmpty()
                ? 0
                : Math.max(0, TIME_EXPIRING_WORD_ATTEMPT_SECONDS - now + wueList.get(0).getFirstAttemptTimestampSeconds());
    }

    @Override
    public void saveWordAttempt(String word, Long chatId) {
        //ну чё нам тут надо, добавить в список попытанных слов чата попытанное слово
        //ии уменьшить количество попыток для попытавшегося юзора
        //давайте этим и займёмся
        var chatAttemptListOpt = databaseConnector.getById(WordleChatAttemptListEntity.class, chatId);
        WordleChatAttemptListEntity wordleChatAttemptListEntity = chatAttemptListOpt.orElseThrow(() -> {
            log.error("No entries for chatId '{}' was found in table!", chatId);
            return new RuntimeException("No entries for chatId = " + chatId);
        });
        wordleChatAttemptListEntity.getAttemptList().add(word);
        databaseConnector.saveOrUpdate(wordleChatAttemptListEntity);
    }

    @Override
    public void decreaseAttemptsCount(Long userId, Long chatId) {
        WordleUserEntity wordleUserEntity = fetchWordleUserEntity(userId, chatId);
        wordleUserEntity.setAttemptsCount(wordleUserEntity.getAttemptsCount() - 1);
        databaseConnector.saveOrUpdate(wordleUserEntity);
    }

    @Override
    public void setRightAnswer(Long chatId) {
        //ну чё нам тут надо, занулить для чата current_word
        //иии сбросить таймштамп попыток для каждого юзера игравшего в чате
        //давайте этим и займёмся

        var wceOpt = databaseConnector.getById(WordleChatCurrentWordEntity.class, chatId);
        WordleChatCurrentWordEntity wordleChatEntity = wceOpt.orElseThrow(() -> {
            log.error("Couldn't set current word: no entries for chatId '{}'", chatId);
            return new RuntimeException("No entries for chatId " + chatId);
        });
        List<WordleUserEntity> playedUsers = databaseConnector.getByQuery(WordleUserEntity.class, "From WordleUserEntity where chatId=" + chatId);

        wordleChatEntity.setCurrentWord(null);
        playedUsers.forEach(wue -> {
            wue.setFirstAttemptTimestampSeconds(0L);
            databaseConnector.saveOrUpdate(wue);
        });
        databaseConnector.saveOrUpdate(wordleChatEntity);
    }

    @Override
    public WordleWordDto getNextRandomWordForChat(Long chatId, int wordLen) {
        //we there must
        //get word list for the current chat
        //if no current chat or no word list for them
        //create new shuffled wordlist
        //create new chat entity with the shuffled wordlist
        //find first word with length closest to wordLen
        //dunno why that behavior
        //maybe if no found word with length of wordLen, just return first found word?
        //and print message that not found with wordLen?
        WordleChatWordListEntity wordList;
        var currentWordListOpt = databaseConnector.getById(WordleChatWordListEntity.class, chatId);
        if (currentWordListOpt.isEmpty() || isNullOrEmpty(currentWordListOpt.get().getWordList())) {
            wordList = new WordleChatWordListEntity(chatId, fetchShuffledWordList());
        } else {
            wordList = currentWordListOpt.get();
        }

        var currentAttemptListOpt = databaseConnector.getById(WordleChatAttemptListEntity.class, chatId);
        WordleChatAttemptListEntity attemptList = currentAttemptListOpt.orElse(new WordleChatAttemptListEntity(chatId, null));
        attemptList.setAttemptList(new ArrayList<>(10));

        var nextRandomWord = takeFromList(wordList.getWordList(), wordLen);

        var currentCurrentWordOpt = databaseConnector.getById(WordleChatCurrentWordEntity.class, chatId);
        WordleChatCurrentWordEntity currentWord = currentCurrentWordOpt.orElse(new WordleChatCurrentWordEntity(chatId, null));
        currentWord.setCurrentWord(nextRandomWord);

        databaseConnector.saveOrUpdate(currentWord);
        databaseConnector.saveOrUpdate(wordList);
        databaseConnector.saveOrUpdate(attemptList);

        return new WordleWordDto(nextRandomWord, wordList.getWordList().size());
    }

    @Override
    public void saveWordleEvent(WordleEventDto we) {
        WordleEventEntity wordleEventEntity = new WordleEventEntity(
                Instant.now().getEpochSecond(),
                we.getChatId(),
                we.getUserId(),
                we.getCurrentWord(),
                we.getAttemptWord(),
                we.isRight()
        );
        databaseConnector.save(wordleEventEntity);
    }

    @Override
    public int addPointsForUser(Long chatId, Long userId, int points) {
        WordleUserEntity wue = fetchWordleUserEntity(userId, chatId);
        Long sumPointsObject = wue.getPoints();
        //не знаю, зачем я это делаю, подстраховка, если вдруг из БД хибер парсит пустое значение в null
        long sumPoints = sumPointsObject == null ? 0L : sumPointsObject.intValue();
        sumPoints += points;
        wue.setPoints(sumPoints);
        databaseConnector.saveOrUpdate(wue);
        return (int) sumPoints;
    }

    @Override
    public WordleUserEntity fetchWordleUserEntity(Long userId, Long chatId) {
        String hql = getHQL(userId, chatId);
        var wueList = databaseConnector.getByQuery(WordleUserEntity.class, hql);
        if (wueList.size() != 1) {
            log.error("For chatId '{}' and userId '{}' count of entries found in table differs from one!", chatId, userId);
            throw new RuntimeException("Wrong number of entries was found for query " + hql);
        }
        return wueList.get(0);
    }

    @Override
    public int getPointsForUserInChat(Long chatId, Long userId) {
        return fetchWordleUserEntity(userId, chatId).getPoints().intValue();
    }

    @Override
    public List<WordleUserEntity> getWordleUsersForChat(Long chatId) {
        return databaseConnector.getByQuery(WordleUserEntity.class, "from WordleUserEntity where chatId=" + chatId);
    }

    private static String getHQL(Long userId, Long chatId) {
        return String.format("from WordleUserEntity where userId=%d and chatId=%d", userId, chatId);
    }

    protected List<String> fetchShuffledWordList() {
        List<WordEntity> wordEntityList = databaseConnector.getByQuery(WordEntity.class, "from WordEntity");

        int partSize = WORD_INDEXES_ARRAY_PART_SIZE;
        int size = wordEntityList.size();
        List<String> shuffledWordList = new ArrayList<>(size);

        for (int i = 0; i < size; i += partSize) {
            List<String> partWordList = new ArrayList<>(partSize);
            int currentPartSize = Math.min(partSize, size - i);
            IntStream.iterate(i, s -> s + 1)
                    .limit(currentPartSize)
                    .mapToObj(wordEntityList::get)
                    .map(WordEntity::getWord)
                    .forEach(partWordList::add);
            Collections.shuffle(partWordList);
            shuffledWordList.addAll(partWordList);
        }

        return shuffledWordList;
    }
}
