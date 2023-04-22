package ru.ikusov.training.telegrambot.model.wordle;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import ru.ikusov.training.telegrambot.dao.wordle.WordleRepository;
import ru.ikusov.training.telegrambot.model.CommonEntity;

import java.util.List;

/**
 * Сущность для хранения данных игры Wordle, общих для одного чата
 */
@Entity
@Table(name = "wordle_chat")
public class WordleChatEntity implements CommonEntity {
    /**
     * Идентификатор чата в телеграм. Это айди, пушо в телеге это айди.
     * Бывает отрицательным (вроде смысл такой, что для групп отрицательный, для личек положительный)
     * (но это не точно)
     */
    @Id
    @Column(name = "chat_id")
    Long chatId;

    /**
     * Загаданное слово для чата.
     */
    @Column(name = "current_word")
    String currentWord;

    /**
     * Список слов для чата. При создании сущности {@link WordleChatEntity} идентичен списку всех слов игры,
     * но разупорядочен специальным образом.
     * Алгоритм разупорядочения следующий.
     * Список всех слов игры (а он изначально упорядочен по убыванию частоты употребления слов в русском языке)
     * делится на блоки по ≈300 слов (количество задаётся параметром
     * {@link WordleRepository#WORD_INDEXES_ARRAY_PART_SIZE}), каждый блок перемешивается случайным образом,
     * а порядок самих блоков не меняется. То есть в списке слов для чата существует крупноблочный порядок
     * по убыванию частоты употребления, но внутри блоков слова перемешаны.
     * Загаданное для чата слово берётся из начала списка (если в начале игры не указано количество букв
     * в загадываемом слове), либо ищется первое встреченное слово с указанным количеством букв. Загаданное
     * для чата слова удаляется из списка слов для чата.
     */
    @Column(name = "word_list")
    List<String> wordList;

    /**
     * Список попыток для чата. Это попытки в рамках одной игры, то есть когда слово угадано, список очищается.
     */
    @Column(name = "attempt_list")
    List<String> attemptList;

    public Long getChatId() {
        return chatId;
    }

    public void setChatId(Long chatId) {
        this.chatId = chatId;
    }

    public String getCurrentWord() {
        return currentWord;
    }

    public void setCurrentWord(String currentWord) {
        this.currentWord = currentWord;
    }

    public List<String> getWordList() {
        return wordList;
    }

    public void setWordList(List<String> wordList) {
        this.wordList = wordList;
    }

    public List<String> getAttemptList() {
        return attemptList;
    }

    public void setAttemptList(List<String> attemptList) {
        this.attemptList = attemptList;
    }

    public WordleChatEntity(Long chatId, String currentWord, List<String> wordList, List<String> attemptList) {
        this.chatId = chatId;
        this.currentWord = currentWord;
        this.wordList = wordList;
        this.attemptList = attemptList;
    }

    public WordleChatEntity() {
    }
}
