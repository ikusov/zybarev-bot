package ru.ikusov.training.telegrambot.botservices;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.ikusov.training.telegrambot.model.CommandType;
import ru.ikusov.training.telegrambot.model.MyBotCommand;
import ru.ikusov.training.telegrambot.services.auxiliary.AuxService;

import static ru.ikusov.training.telegrambot.statik.Constants.SUNFLOWER_MESSAGE;

@Component
@RequiredArgsConstructor
public class AdminCommandMessageHandler extends CommandMessageHandler {
    //todo: some day get'em from properties?
    private static final long ADMIN_USER_ID = 349513007L;
    private static final String COPY_COMMAND = "copy";

    private final AuxService auxService;

    @Override
    public BotReaction handleCommand(MyBotCommand command) {
        //very simple temporary admin for copying data from redis to postgres
        String chatId = command.chatId().toString();
        var topicId = command.topicId();
        Long userId = command.user().getId();
        String commandParams = command.params().strip();
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

    @Override
    protected CommandType getSupportedCommandType() {
        return CommandType.ADMIN;
    }
}
