package ru.ikusov.training.telegrambot.botservices;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import ru.ikusov.training.telegrambot.model.MyBotCommand;
import ru.ikusov.training.telegrambot.services.wordle.WordleStatService;

import java.util.Set;

import static ru.ikusov.training.telegrambot.MainClass.IS_TEST_MODE;
import static ru.ikusov.training.telegrambot.MainClass.TEST_CHAT_ID;

@Component
@Order(183)
public class WordleStatCommandMessageHandler extends CommandMessageHandler {
    private final Logger log = LoggerFactory.getLogger(this.getClass());
    private final Set<String> commandVariants = Set.of("/wordlestat", "/wordstat", "/словастат");
    private final WordleStatService wordleStatService;

    @Autowired
    public WordleStatCommandMessageHandler(WordleStatService wordleStatService) {
        this.wordleStatService = wordleStatService;
    }

    @Override
    protected void addHelp() {
        String help = commandVariants.stream().reduce((s1, s2) -> s1 + ", " + s2).orElse("");
        help += " - Выводит статистику по игре Wordle.\n";
        helpString = help + helpString;
    }

    @Override
    protected Set<String> getCommandVariants() {
        return commandVariants;
    }

    @Override
    public BotReaction handleCommand(MyBotCommand command) {
        String chatId = command.getChatId().toString();
        var topicId = command.getTopicId();
        if (IS_TEST_MODE && !chatId.equals(TEST_CHAT_ID.toString())) {
            return new BotMessageSender(
                    chatId,
                    topicId,
                    "Игра недоступна в связи с проведением профилактических работ. Попробуйте позже!"
            );
        }

        String fMsg;
        try {
            fMsg = wordleStatService.getStat(command.getChat(), command.getUser());
            return new BotFormattedMessageSender(chatId, topicId, fMsg);
        } catch (Exception e) {
            log.error("Error while wordle stat handling!", e);
            return new BotMessageSender(chatId, topicId, "Неизвестная ошибка! Попробуйте ещё раз.");
        }
    }
}
