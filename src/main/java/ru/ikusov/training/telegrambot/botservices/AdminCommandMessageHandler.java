package ru.ikusov.training.telegrambot.botservices;

import lombok.RequiredArgsConstructor;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import ru.ikusov.training.telegrambot.botservices.annotation.ExcludeFromHelp;
import ru.ikusov.training.telegrambot.model.MyBotCommand;
import ru.ikusov.training.telegrambot.services.auxiliary.AuxService;

import java.util.Set;

import static ru.ikusov.training.telegrambot.statik.Constants.SUNFLOWER_MESSAGE;

@Component
@Order(200)
@RequiredArgsConstructor
public class AdminCommandMessageHandler extends CommandMessageHandler {
    //todo: some day get'em from properties?
    private static final long ADMIN_USER_ID = 349513007L;
    private static final String COPY_COMMAND = "copy";

    private final AuxService auxService;

    @Override
    protected Set<String> getCommandVariants() {
        return Set.of("/admin");
    }

    @Override
    @ExcludeFromHelp
    protected String getHelpString() {
        return "админка";
    }

    @Override
    public BotReaction handleCommand(MyBotCommand command) {
        //very simple temporary admin for copying data from redis to postgres
        String chatId = command.getChatId().toString();
        var topicId = command.getTopicId();
        Long userId = command.getUser().getId();
        String commandParams = command.getParams().strip();
        if (userId == ADMIN_USER_ID && commandParams.equals(COPY_COMMAND)) {
            String result = auxService.calculateAndSavePoints();
            return new BotMessageSender(chatId, topicId, result);
        } else {
            return new BotFormattedMessageSender(
                    chatId,
                    topicId, SUNFLOWER_MESSAGE
            );
        }
    }
}
