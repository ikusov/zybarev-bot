package ru.ikusov.training.telegrambot.botservices;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import ru.ikusov.training.telegrambot.model.MyBotCommand;
import ru.ikusov.training.telegrambot.services.auxiliary.AuxService;

import java.util.Set;

import static ru.ikusov.training.telegrambot.statik.Constants.SUNFLOWER_MESSAGE;

@Component
@Order(200)
public class AdminCommandMessageHandler extends CommandMessageHandler {
    //todo: some day get'em from properties?
    private static final long ADMIN_USER_ID = 349513007L;
    private static final String COPY_COMMAND = "copy";
    private final AuxService auxService;
    private final Set<String> commandVariants = Set.of("/admin");

    @Autowired
    public AdminCommandMessageHandler(AuxService auxService) {
        this.auxService = auxService;
    }

    @Override
    protected Set<String> getCommandVariants() {
        return commandVariants;
    }

    @Override
    protected void addHelp() {
        String help = commandVariants.stream().reduce((s1, s2) -> s1 + ", " + s2).orElse("");
        help += " - админка.\n";
        helpString = help + helpString;
    }

    @Override
    public BotReaction handleCommand(MyBotCommand command) {
        //very simple temporary admin for copying data from redis to postgres
        String chatId = command.getChatId();
        Long userId = command.getUser().getId();
        String commandParams = command.getParams().strip();
        if (userId == ADMIN_USER_ID && commandParams.equals(COPY_COMMAND)) {
            String result = auxService.calculateAndSavePoints();
            return new BotMessageSender(chatId, result);
        } else {
            return new BotFormattedMessageSender(
                    chatId,
                    SUNFLOWER_MESSAGE
            );
        }
    }
}
