package ru.ikusov.training.telegrambot.services.auxiliary;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import ru.ikusov.training.telegrambot.dao.DatabaseConnector;
import ru.ikusov.training.telegrambot.model.ChatEntity;
import ru.ikusov.training.telegrambot.model.wordle.WordEntity;
import ru.ikusov.training.telegrambot.model.wordle.WordleChatWordListEntity;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Вспомогательная логика для всяких админских разовых действий.
 */
@Slf4j
@Component
public class AuxService {
    private final DatabaseConnector databaseConnector;

    @Autowired
    public AuxService(DatabaseConnector databaseConnector) {
        this.databaseConnector = databaseConnector;
    }

    public String findWordFromWordleTables(String word) {
        String response = "";

        //поискать слово в списке слов
        log.debug("Попытка поиска слова '{}' в общей базе слов...", word);
        var foundInCommon = databaseConnector.getByQuery(WordEntity.class, "from WordEntity where word = '%s'".formatted(word));
        log.debug("Слово '{}' {}найдено в общей базе слов!", word, foundInCommon.isEmpty() ? "не " : "");
        if (!foundInCommon.isEmpty()) {
            response += "\n- общем списке слов!";
        }

        //поискать слово в списках слов для чатиков
        log.debug("Выгребаю списки слов для чатиков...");
        var chatWordLists = databaseConnector.getByQuery(WordleChatWordListEntity.class, "from WordleChatWordListEntity");
        log.debug("Вытащил {} списков слов!", chatWordLists.size());
        for (var chatWordListEntity : chatWordLists) {
            log.debug("Проверяю наличие слова '{}' в списке слов для чата '{}'...", word, chatWordListEntity.getChatId());
            if (chatWordListEntity.getWordList().contains(word)) {
                var chatName = databaseConnector.getByQuery(ChatEntity.class, "from ChatEntity where id = %d".formatted(chatWordListEntity.getChatId()));
                response += "\n- в списке чатика '%s'".formatted(chatName);
            }
        }

        return response.isEmpty()
                ? "Слово '%s' не найдено в БД!".formatted(word)
                : "Слово '%s' найдено:".formatted(word) + response;
    }

    public String deleteWords(List<String> words) {
        Map<String, Integer> commonDeleted = new HashMap<>();
        Map<String, Map<String, Integer>> chatsDeleted = new HashMap<>();

        for (var word : words) {
            int commonCount = deleteFromWordleWord(word);
            commonDeleted.put(word, commonCount);

            Map<String, Integer> chatsCount = deleteFromChatWordLists(word);
            chatsDeleted.put(word, chatsCount);
        }

        return "\nWords deletion stats:\n" + commonDeleted + "\n" + chatsDeleted;
    }

    private Map<String, Integer> deleteFromChatWordLists(String word) {
        //поискать слово в списках слов для чатиков
        log.debug("Выгребаю списки слов для чатиков...");
        var chatWordLists = databaseConnector.getByQuery(WordleChatWordListEntity.class, "from WordleChatWordListEntity");
        log.debug("Вытащил {} списков слов!", chatWordLists.size());

        Map<String, Integer> chatDeletions = new HashMap<>();
        for (var chatWordListEntity : chatWordLists) {
            List<String> chatWordList = chatWordListEntity.getWordList();

            int deletionCount = 0;
            while (chatWordList.remove(word)) {
                deletionCount++;
            }

            if (deletionCount > 0) {
                databaseConnector.saveOrUpdate(chatWordListEntity);
            }

            chatDeletions.put(chatWordListEntity.getChatId().toString(), deletionCount);
        }

        return chatDeletions;
    }

    private int deleteFromWordleWord(String word) {
        return databaseConnector.deleteByQuery(WordEntity.class, "word", word);
    }

}
