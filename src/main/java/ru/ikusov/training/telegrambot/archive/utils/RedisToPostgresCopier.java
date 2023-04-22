package ru.ikusov.training.telegrambot.archive.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;
import ru.ikusov.training.telegrambot.dao.DatabaseConnector;
import ru.ikusov.training.telegrambot.model.wordle.WordleChatEntity;
import ru.ikusov.training.telegrambot.model.wordle.WordleUserEntity;

import java.net.URI;
import java.time.Instant;
import java.util.*;
import java.util.stream.Collectors;

import static ru.ikusov.training.telegrambot.dao.wordle.WordleRepository.TIME_EXPIRING_WORD_ATTEMPT_SECONDS;

@Deprecated
//@Component
public class RedisToPostgresCopier {
    private static final Logger log = LoggerFactory.getLogger(RedisToPostgresCopier.class);
    private static final String CURRENT_WORD_PATTERN = "current_word:";
    private static final String KEY_WORDS_ENTRY = "words";
    private final DatabaseConnector databaseConnector;
    private final Jedis jedis;

    @Autowired
    public RedisToPostgresCopier(DatabaseConnector databaseConnector) {
        this.databaseConnector = databaseConnector;
        JedisPool jedisPool = getJedisPool();
        this.jedis = jedisPool.getResource();
    }

    public String copyData() {
        StringBuilder report = new StringBuilder(1000);

        log.info("Starting copying data from Redis to Postgres...");

        log.info("Try to get word list from Redis...");
        List<String> wordList = jedis.lrange("words", 0, -1);
        int wordListSize = wordList.size();
        int partSizeForLogging = wordListSize / 20;
        log.info("Word list was successfully getted! We have {} words!" +
                " We now will try to save words to database...", wordListSize);

        for (int i = 0; i < wordListSize; i++) {
            String word = wordList.get(i);
            if (i % partSizeForLogging == 0) {
                log.info("Trying to save {} word of {}...", i, wordListSize);
            }
//            databaseConnector.save(new WordEntity(word));
            if (i % partSizeForLogging == 0) {
                log.info("NOT SAVING ACTUALLY BECAUSE ALL WAS SAVED!");
                log.info("{} word of {} was saved to DB!", i, wordListSize);
            }
        }
        report.append("Saved to DB words: ").append(wordListSize).append("\n");

        log.info("Try to get chat list from Redis...");
        List<String> chatIdList = getChatList();
        Map<String, String> nowPlayingChatMap = new HashMap<>();
        log.info("Chatlist successfully getted: '{}'", chatIdList);

        //constructing and saving wordle chat entities
        for (String chatId : chatIdList) {
            WordleChatEntity wordleChatEntity;

            log.info("Fetching current word for chat {}...", chatId);
            String currentWordId = fetchCurrentWordId(chatId);
            log.info("Successfully fetched current word ID '{}'! ", currentWordId);

            if (!currentWordId.equals("")) {
                log.info("Current word ID is not empty, saving it to now playing map...");
                nowPlayingChatMap.put(chatId, currentWordId);
            }

            log.info("Trying to fetching current word by word ID '{}'...", currentWordId);
            String currentWord = fetchWordById(currentWordId);
            log.info("Successfully fetched current word '{}' for chat '{}'!" +
                    " Fetching word list...", currentWord, chatId);
            List<String> wordListForChat = fetchWordListByKey("word_id_list:" + chatId);
            log.info("Successfully fetched {} words for chat '{}'!" +
                    " Fetching tried words list...", wordListForChat.size(), chatId);
            List<String> attemptList = fetchAttemptListByKey("tried_words:" + chatId + ":" + currentWordId);
            log.info("Successfully fetched tried words {}" +
                    " Trying to construct WORDLE CHAT ENTITY! " +
                    " There MAY BE NumberFormatException! ALARMA!", attemptList);
            wordleChatEntity = new WordleChatEntity(Long.valueOf(chatId), currentWord, wordListForChat, attemptList);
            log.info("Pooh! No exception. Try to save entity to database...");
            log.info("NOT SAVING CAUSE WAS SAVED!");
//            databaseConnector.save(wordleChatEntity);
            log.info("Wordle Chat Entity for chat ID '{}' was saved!", chatId);
        }
        report.append("Saved to DB anytime wordle played chats: ").append(chatIdList.size()).append("\n");

        //copying playing users data (attempts count, ban time)
        int usersCount = 0;
        for (String chatId : nowPlayingChatMap.keySet()) {
            String wordId = nowPlayingChatMap.get(chatId);
            String pattern = "attempts_count:" + chatId + ":*:" + wordId;
            log.info("Trying to get key list for pattern '{}'...", pattern);
            List<String> attemptsCountKeyList = new ArrayList<>(jedis.keys(pattern));
            log.info("Key list has been successfully fetched: '{}'", attemptsCountKeyList);
            for (String attemptsCountKey : attemptsCountKeyList) {
                log.info("Trying to get attempts count by key '{}'...", attemptsCountKey);
                String attemptsCountString = jedis.get(attemptsCountKey);
                log.info("For chat/user '{}' attempts count is '{}", attemptsCountKey, attemptsCountString);
                if (attemptsCountString != null) {
                    log.info("Attempts count is not null, we now try to construct new WordleUserEntity!" +
                            " There MAY BE A LOT OF EXCEPTIONS! ALARMA!");
                    String userIdString = attemptsCountKey.split(":")[2];
                    long timeStamp = Instant.now().getEpochSecond() + jedis.ttl(attemptsCountKey) - TIME_EXPIRING_WORD_ATTEMPT_SECONDS;
                    WordleUserEntity wordleUserEntity = new WordleUserEntity(
                            Long.valueOf(userIdString),
                            Long.valueOf(chatId),
                            Integer.valueOf(attemptsCountString),
                            timeStamp);
                    log.info("Pooh! No exception. Try to save entity to database...");
                    databaseConnector.save(wordleUserEntity);
                    usersCount++;
                    log.info("Wordle Chat Entity for chat ID '{}' was saved!", chatId);
                }
            }
        }
        report.append("Saved to DB anytime wordle played users: ").append(usersCount);
        return report.toString();
    }

    private List<String> fetchWordListByKey(String key) {
        var idList = jedis.lrange(key, 0, -1);
        List<String> wordList = new ArrayList<>(idList.size());
        idList.forEach(wordId -> wordList.add(fetchWordById(wordId)));
        return wordList;
    }

    private List<String> fetchAttemptListByKey(String key) {
        return jedis.lrange(key, 0, -1);
    }

    private String fetchCurrentWordId(String chatId) {
        return jedis.get(CURRENT_WORD_PATTERN + chatId);
    }

    private String fetchWordById(String wordId) {
        return wordId.equals("")
                ? ""
                : jedis.lindex(KEY_WORDS_ENTRY, Long.parseLong(wordId));
    }

    private List<String> getChatList() {
        return jedis.keys(CURRENT_WORD_PATTERN + "*").stream()
                .map(s -> s.substring(CURRENT_WORD_PATTERN.length()))
                .collect(Collectors.toList());
    }

    private JedisPool getJedisPool() {
        String redisSystemEnvironmentVariableName = "REDIS_URL";
        log.info("Trying to get redis uri from system env variable {}...", redisSystemEnvironmentVariableName);
        String uriString = System.getenv(redisSystemEnvironmentVariableName);
        Objects.requireNonNull(uriString);
        log.info("Redis url was successfully getted: {}", uriString);
        log.info("Trying to create URI from getted uri string...");
        URI uri = URI.create(uriString);
        log.info("URI was successfylly created!");

        JedisPoolConfig poolConfig = new JedisPoolConfig();
        poolConfig.setMaxTotal(10);
        poolConfig.setMaxIdle(5);
        poolConfig.setMinIdle(1);
        poolConfig.setTestOnBorrow(true);
        poolConfig.setTestOnReturn(true);
        poolConfig.setTestWhileIdle(true);
        return new JedisPool(poolConfig, uri);
    }
}
