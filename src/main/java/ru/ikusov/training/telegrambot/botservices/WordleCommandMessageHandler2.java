package ru.ikusov.training.telegrambot.botservices;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.annotation.Order;
import ru.ikusov.training.telegrambot.botservices.utils.TelegramChatUtils;
import ru.ikusov.training.telegrambot.model.MyBotCommand;
import ru.ikusov.training.telegrambot.services.wordle.WordleMessageProvider;
import ru.ikusov.training.telegrambot.services.wordle.WordleService2;
import ru.ikusov.training.telegrambot.services.wordle.WordleStatService2;
import ru.ikusov.training.telegrambot.services.wordle.dto.WordleChatStatDto;
import ru.ikusov.training.telegrambot.services.wordle.dto.WordleStartGameDto;

import java.util.List;
import java.util.Set;

//TODO: чот решил перепилить всю логику полностью

//@Component
@Order(185)
@Slf4j
@RequiredArgsConstructor
public class WordleCommandMessageHandler2 extends CommandMessageHandler {
    private final static List<String> STAT_PARAM_LIST = List.of("стат", "stat");
    private final Set<String> commandVariants = Set.of("/wordle", "/words", "/слова");

    private final WordleStatService2 wordleStatService2;
    private final WordleService2 wordleService2;

    @Override
    protected void addHelp() {
        String help = commandVariants.stream().reduce((s1, s2) -> s1 + ", " + s2).orElse("");
        help += " - Игра в слова по типу Wordle. Параметром можно задать длину слова (от 4 до 8 символов), " +
                "параметр 'стат' выводит статистику по игре.\n";
        helpString = help + helpString;
    }

    @Override
    protected Set<String> getCommandVariants() {
        return commandVariants;
    }

    @Override
    public BotReaction handleCommand(MyBotCommand command) {
        String extendedChatId = TelegramChatUtils.buildExtendedChatId(command.getChatId(), command.getTopicId());

        Long chatId = command.getChatId();
        Long userId = command.getUser().getId();
        var topicId = command.getTopicId();

        String commandParams = command.getParams().strip();

        if (isStatParam(commandParams)) {
            WordleChatStatDto wordleChatStatDto = wordleStatService2.getStat(extendedChatId, userId);
            return new BotFormattedMessageSender(
                    chatId.toString(),
                    topicId,
                    WordleMessageProvider.formattedStatMessage(wordleChatStatDto));
        }

        var wordLen = getWordLength(commandParams);

        try {
            WordleStartGameDto wordleStartGameDto = wordleService2.startGame(extendedChatId, userId, wordLen);
            return new BotFormattedMessageSender(
                    chatId.toString(),
                    topicId,
                    WordleMessageProvider.formattedStartGameMessage(wordleStartGameDto));
        } catch (Exception e) {
            log.error("Error while start game handling: {}", e.getMessage());
            return new BotMessageSender(chatId.toString(), topicId, "Неизвестная ошибка! Попробуйте ещё раз.");
        }
    }

    private boolean isStatParam(String commandParams) {
        return STAT_PARAM_LIST.stream()
                .map(commandParams::contains)
                .reduce((c1, c2) -> c1 || c2)
                .orElse(false);
    }

    private int getWordLength(String commandParams) {
        int wordLength = 0;
        try {
            wordLength = Integer.parseInt(commandParams.strip());
        } catch (NumberFormatException ignore) {
        }

        return wordLength;
    }
}
