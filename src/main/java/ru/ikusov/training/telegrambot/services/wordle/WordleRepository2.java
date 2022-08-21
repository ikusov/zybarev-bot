package ru.ikusov.training.telegrambot.services.wordle;

import org.springframework.stereotype.Component;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;
import ru.ikusov.training.telegrambot.rabbit.RabbitPublisher;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import java.net.URI;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Component
public class WordleRepository2 {
    private static final int WORD_INDEXES_ARRAY_PART_SIZE = 300;
    private static final String KEY_WORDS_ENTRY = "words";
    private static final long TIME_EXPIRING_WORD_ATTEMPT_SECONDS = 24 * 3600;

    private static final String RABBIT_MSG = "For chat id %s word-to-guess number %s";
    private final String redisSystemEnvironmentVariableName = "REDIS_URL";
    private final JedisPool myJedisPool;
    private final RabbitPublisher rabbitPublisher;

    public WordleRepository2(RabbitPublisher rabbitPublisher) {
        this.rabbitPublisher = rabbitPublisher;
        myJedisPool = getJedisPool();
    }

    //lot of boilerplate code for heroku redis connection
    private JedisPool getJedisPool() {
        try {
            TrustManager bogusTrustManager = new X509TrustManager() {
                public X509Certificate[] getAcceptedIssuers() {
                    return null;
                }

                public void checkClientTrusted(X509Certificate[] certs, String authType) {
                }

                public void checkServerTrusted(X509Certificate[] certs, String authType) {
                }
            };

            SSLContext sslContext = SSLContext.getInstance("SSL");
            sslContext.init(null, new TrustManager[]{bogusTrustManager}, new java.security.SecureRandom());

            HostnameVerifier bogusHostnameVerifier = (hostname, session) -> true;

            JedisPoolConfig poolConfig = new JedisPoolConfig();
            poolConfig.setMaxTotal(10);
            poolConfig.setMaxIdle(5);
            poolConfig.setMinIdle(1);
            poolConfig.setTestOnBorrow(true);
            poolConfig.setTestOnReturn(true);
            poolConfig.setTestWhileIdle(true);

            return new JedisPool(poolConfig,
                    URI.create(System.getenv(redisSystemEnvironmentVariableName)),
                    sslContext.getSocketFactory(),
                    sslContext.getDefaultSSLParameters(),
                    bogusHostnameVerifier);

        } catch (NoSuchAlgorithmException | KeyManagementException e) {
            throw new RuntimeException("Cannot obtain Redis connection!", e);
        }
    }

    public String getCurrentWordForChat(Long chatId) {
        String word;

        try (var jedis = myJedisPool.getResource()) {
            var wordId = getCurrentWordId(jedis, chatId);
            if (wordId == null || wordId.isEmpty()) {
                return "";
            }

            word = jedis.lindex(KEY_WORDS_ENTRY, Long.parseLong(wordId));
        } catch (NumberFormatException e) {
            throw new RuntimeException("Error while getting current word for chat " + chatId + ":" +
                    "number format exception while parsing word id: " + e.getMessage(), e);
        }

        return word == null ? "" : word;
    }

    public List<String> getTriedWordsForChat(Long chatId) {
        List<String> words;

        try (var jedis = myJedisPool.getResource()) {
            var currentWordId = getCurrentWordId(jedis, chatId);

            var key = keyTriedWords(chatId, currentWordId);
            words = jedis.lrange(key, 0, -1);
        }

        return words;
    }

    public boolean isAnyWordAttempts(Long chatId) {
        long triedWordsSize;

        try (var jedis = myJedisPool.getResource()) {
            String currentWordId = getCurrentWordId(jedis, chatId);

            var key = keyTriedWords(chatId, currentWordId);
            triedWordsSize = jedis.llen(key);
        }

        return triedWordsSize > 0;
    }

    public int getOrCreateWordAttempt(Long userId, Long chatId, int currentAllowedAttempts) {
        String wordAttemptsCount;

        try (var jedis = myJedisPool.getResource()) {
            String wordId = getCurrentWordId(jedis, chatId);
            exceptionIfNullOrEmpty(wordId, "Error while getting word attempt: empty or null word id for chat " + chatId);

            var key = keyAttemptsCount(chatId, userId, wordId);
            wordAttemptsCount = jedis.get(key);

            if (wordAttemptsCount == null) {
                wordAttemptsCount = String.valueOf(currentAllowedAttempts);
                jedis.setex(key, TIME_EXPIRING_WORD_ATTEMPT_SECONDS, wordAttemptsCount);
            }
        }

        return Integer.parseInt(wordAttemptsCount);
    }

    public long getWordAttemptTTL(Long userId, Long chatId) {
        long wordAttemptsTTL;

        try (var jedis = myJedisPool.getResource()) {
            String wordId = getCurrentWordId(jedis, chatId);

            var key = keyAttemptsCount(chatId, userId, wordId);
            wordAttemptsTTL = jedis.ttl(key);
        }

        return wordAttemptsTTL < 0 ? 0 : wordAttemptsTTL;
    }

    public void saveWordAttempt(String word, Long userId, Long chatId) {
        try (var jedis = myJedisPool.getResource()) {
            String wordId = getCurrentWordId(jedis, chatId);
            exceptionIfNullOrEmpty(wordId, "Error while saving word attempt: empty or null word id for chat " + chatId);

            var key = keyAttemptsCount(chatId, userId, wordId);
            jedis.decr(key);

            key = keyTriedWords(chatId, wordId);
            jedis.rpush(key, word);
        }
    }

    public String getNextRandomWordForChat(Long chatId) {
        String word;

        try (var jedis = myJedisPool.getResource()) {
            var key = keyWordIdList(chatId);
            var listExists = jedis.exists(key);

            if (!listExists) {
                var size = (int) jedis.llen(KEY_WORDS_ENTRY);
                String[] indexesArray = getShuffledIndexesArray(chatId, size);

                jedis.rpush(key, indexesArray);
            }

            String wordIdString = jedis.lpop(key);
            word = jedis.lindex(KEY_WORDS_ENTRY, Integer.parseInt(wordIdString));

            key = keyCurrentWord(chatId);
            jedis.set(key, wordIdString);
        }

        return word;
    }

    public void setRightAnswer(Long chatId) {
        try (var jedis = myJedisPool.getResource()) {
            var key = keyCurrentWord(chatId);
            jedis.set(key, "");
//            jedis.incr(CURRENT_WORD_INDEX_FOR + chatId);
        }
    }

    //TODO: начал работу по сбору статистики угаданных словей
    public void setRightAnswer(Long chatId, Long userId) {
        try (var jedis = myJedisPool.getResource()) {
            var key = keyCurrentWord(chatId);
            jedis.set(key, "");

            key = keyRightAnsweredUserList(chatId);
            jedis.rpush(key, userId.toString());
        }
    }

    public String[] getShuffledIndexesArray(Long chatId, int size) {
        List<String> bigIndexesList = new ArrayList<>(size);

        var random = new Random(chatId);
        for (int i = 0; i < size; i += WORD_INDEXES_ARRAY_PART_SIZE) {
            var seed = i;
            var limit = Math.min(WORD_INDEXES_ARRAY_PART_SIZE, size - i);
            var indexesList = IntStream.iterate(seed, s -> s + 1)
                    .boxed()
                    .map(Object::toString)
                    .limit(limit)
                    .collect(Collectors.toList());
            Collections.shuffle(indexesList, random);
            bigIndexesList.addAll(indexesList);
        }

        return bigIndexesList.toArray(new String[0]);
    }

    private String getCurrentWordId(Jedis jedis, Long chatId) {
        var key = keyCurrentWord(chatId);
        return jedis.get(key);
    }

    private String keyCurrentWord(Long chatId) {
        return "current_word:" + chatId;
    }

    private String keyTriedWords(Long chatId, String currentWordId) {
        return "tried_words:" + chatId + ":" + currentWordId;
    }

    private String keyAttemptsCount(Long chatId, Long userId, String wordId) {
        return "attempts_count:" + chatId + ":" + userId + ":" + wordId;
    }

    private String keyWordIdList(Long chatId) {
        return "word_id_list:" + chatId;
    }

    private String keyRightAnsweredUserList(Long chatId) {
        return "right_answered_user_list:" + chatId;
    }

    private void exceptionIfNullOrEmpty(String word, String text) {
        if (word == null || word.isEmpty()) {
            throw new RuntimeException(text);
        }
    }

    public String getNextRandomWordForChat(Long chatId, int wordLen) {
        String word = "";

        try (var jedis = myJedisPool.getResource()) {
            var key = keyWordIdList(chatId);
            var listExists = jedis.exists(key);

            if (!listExists) {
                var size = (int) jedis.llen(KEY_WORDS_ENTRY);
                String[] indexesArray = getShuffledIndexesArray(chatId, size);

                jedis.rpush(key, indexesArray);
            }

            String wordIdString = null;
            //логика поиска слова нужной длины здесь

            //длина слова не может быть нулевой или отрицательной,
            if (wordLen > 0) {
                int minDif = Integer.MAX_VALUE;
                for (long i = 0; minDif > 0; i++) {
                    String nextWordIdString = jedis.lindex(key, i);

                    if (nextWordIdString == null) {
                        break;
                    }

                    String nextWord = jedis.lindex(KEY_WORDS_ENTRY, Integer.parseInt(nextWordIdString));

                    int dif = Math.abs(nextWord.length() - wordLen);

                    if (dif < minDif) {
                        minDif = dif;
                        word = nextWord;
                        wordIdString = nextWordIdString;
                    }
                }
            }

            //если длина слова нулевая или отрицательная
            //или если что-то пошло не так
            if (wordIdString == null) {
                wordIdString = jedis.lpop(key);
                word = jedis.lindex(KEY_WORDS_ENTRY, Integer.parseInt(wordIdString));
            } else {
                jedis.lrem(key, 0, wordIdString);
            }

            key = keyCurrentWord(chatId);
            jedis.set(key, wordIdString);

            if (rabbitPublisher != null) {
                String msg = String.format(RABBIT_MSG, chatId.toString(), wordIdString);
                rabbitPublisher.publish(msg);
            }
        }

        return word;
    }
}
