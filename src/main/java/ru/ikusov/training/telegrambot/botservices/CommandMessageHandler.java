package ru.ikusov.training.telegrambot.botservices;

import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.telegram.telegrambots.meta.api.objects.Message;
import ru.ikusov.training.telegrambot.Bot;
import ru.ikusov.training.telegrambot.model.CommandType;
import ru.ikusov.training.telegrambot.model.MyBotCommand;
import ru.ikusov.training.telegrambot.services.BotStatisticService;
import ru.ikusov.training.telegrambot.services.UserNameGetter;

/**
 * abstract class for command-type (starting from '/') messages handlers
 * contains shared logics, such as checking if message is command, splitting message to
 * command and params etc.
 */

@Slf4j
@NoArgsConstructor
public abstract class CommandMessageHandler implements MessageHandler {
    private Bot bot;
    private BotStatisticService botStatisticService;

    @Autowired
    public void setBot(Bot bot) {
        this.bot = bot;
    }

    @Autowired
    public void setBotStatisticService(BotStatisticService botStatisticService) {
        this.botStatisticService = botStatisticService;
    }

    private MyBotCommand command;

    @Override
    public void handleMessage(Message message) {
        command = MyBotCommand.build(message, bot.getBotUsername());
        if (command == null) {
            return;
        }

        //if no variants - that's for unknown command handler -
        //or
        //if the inheritor instance's command variants contains the command
        if (getSupportedCommandType() == command.commandType()) {
            this.log();
            botStatisticService.saveCommandInvoke(command);
            BotReaction botReaction = handleCommand(command);
            botReaction.react(bot);
            botReaction.log();
        }
    }

    protected void log() {
        log.info(
                "COMMAND ChatId: '{}' TopicId: '{}' ChatName: '{}' UserId: '{}' UserName:'{}' Command: {}",
                command.chatId(),
                command.topicId(),
                command.chat().getTitle(),
                command.user().getId(),
                UserNameGetter.getUserName(command.user()),
                command.commandType() + " " + command.params()
        );
    }

    //the method to be implemented by descendants
    public abstract BotReaction handleCommand(MyBotCommand command);

    protected abstract CommandType getSupportedCommandType();
}
