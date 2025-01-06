package ru.ikusov.training.telegrambot.botservices;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.ikusov.training.telegrambot.model.CommandType;
import ru.ikusov.training.telegrambot.model.MyBotCommand;
import ru.ikusov.training.telegrambot.services.auxiliary.AuxService;
import ru.ikusov.training.telegrambot.utils.MyString;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static ru.ikusov.training.telegrambot.statik.Constants.SUNFLOWER_MESSAGE;

@Slf4j
@Component
@RequiredArgsConstructor
public class AdminCommandMessageHandler extends CommandMessageHandler {
    //todo: some day get'em from properties?
    private static final long ADMIN_USER_ID = 349513007L;
    private static final String DELETE_COMMAND = "delete";
    private static final String FIND_COMMAND = "find";

    private final AuxService auxService;

    @Override
    public BotReaction handleCommand(MyBotCommand command) {
        Long userId = command.user().getId();
        String chatId = command.chatId().toString();
        var topicId = command.topicId();

        if (userId != ADMIN_USER_ID) {
            return sunflowerMessage(chatId, topicId);
        }

        String[] commandParams = command.params().strip().split("\\s", 2);
        String subCommand = commandParams[0];
        String paramString = commandParams.length > 1 ? commandParams[1] : "";

        if (subCommand.equals(DELETE_COMMAND)) {
            log.info("Получена команда '{}'", DELETE_COMMAND);

            List<String> words = Arrays.stream(paramString.strip().split("\\s"))
                    .map(MyString::brutalProcessing)
                    .filter(s -> !s.isBlank())
                    .toList();

            String result = auxService.deleteWords(words);

            return new BotMessageSender(chatId, topicId, result);
        }

        if (subCommand.equals(FIND_COMMAND)) {
            log.info("Получена команда '{}'", FIND_COMMAND);

            Optional<String> wordOptional = Arrays.stream(paramString.strip().split("\\s"))
                    .map(MyString::brutalProcessing)
                    .filter(s -> !s.isBlank())
                    .findFirst();

            String result = wordOptional.isPresent()
                    ? auxService.findWordFromWordleTables(wordOptional.get())
                    : "Передано некорректное значение параметра для поиска: '%s'".formatted(paramString);

            return new BotMessageSender(chatId, topicId, result);
        }

        return sunflowerMessage(chatId, topicId);
    }

    @Override
    protected CommandType getSupportedCommandType() {
        return CommandType.ADMIN;
    }

    private static BotMessageSender sunflowerMessage(String chatId, Integer topicId) {
        return new BotFormattedMessageSender(
                chatId,
                topicId, SUNFLOWER_MESSAGE
        );
    }
}
