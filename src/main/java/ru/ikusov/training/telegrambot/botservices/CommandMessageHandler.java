package ru.ikusov.training.telegrambot.botservices;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.telegram.telegrambots.meta.api.objects.Message;
import ru.ikusov.training.telegrambot.Bot;
import ru.ikusov.training.telegrambot.botservices.annotation.ExcludeFromHelp;
import ru.ikusov.training.telegrambot.model.MyBotCommand;
import ru.ikusov.training.telegrambot.services.UserNameGetter;

import java.util.HashSet;
import java.util.Set;

/**
 * abstract class for command-type (starting from '/') messages handlers
 * contains shared logics, such as checking if message is command, splitting message to
 * command and params etc.
 */

public abstract class CommandMessageHandler implements MessageHandler {
    protected final Logger log = LoggerFactory.getLogger(this.getClass());
    private Bot bot;

    @Autowired
    public void setBot(Bot bot) {
        this.bot = bot;
    }

    public CommandMessageHandler() {
    }

    private MyBotCommand command;

    //static collection of registered commands
    protected static Set<String> registeredCommands = new HashSet<>();

    //static help string
    protected static String helpString = "";
    protected static boolean helpFormed = false;

    private final Set<String> commandVariants = Set.of("/bakpak");

    @Override
    public void handleMessage(Message message) {
        command = MyBotCommand.buildCommand(message, bot.getBotUsername());
        if (command == null) {
            return;
        }

        //add help for the command
        if(!helpFormed) {
            addHelp();
        }

        //every inheritor has its own command variants
        Set<String> commandVariants = getCommandVariants();

        //add command handler commands to registry
        registeredCommands.addAll(commandVariants);

        //if no variants - that's for unknown command handler -
        //or
        //if the inheritor instance's command variants contains the command
        if (commandVariants.isEmpty() || commandVariants.contains(command.getCommand())) {
            this.log();
            BotReaction botReaction = handleCommand(command);
            botReaction.react(bot);
            botReaction.log();
        }
    }

    protected void log() {
        log.info(
                "COMMAND ChatId: '{}' TopicId: '{}' ChatName: '{}' UserId: '{}' UserName:'{}' Command: {}",
                command.getChatId(),
                command.getTopicId(),
                command.getChat().getTitle(),
                command.getUser().getId(),
                UserNameGetter.getUserName(command.getUser()),
                command.getCommand() + " " + command.getParams()
        );
    }

    //the method to be implemented by descendants
    public abstract BotReaction handleCommand(MyBotCommand command);

    protected String getHelpString() {
        return "";
    }

    protected void addHelp() {
        boolean excludeFromHelp = true;

        try {
            excludeFromHelp = this.getClass()
                    .getDeclaredMethod("getHelpString")
                    .isAnnotationPresent(ExcludeFromHelp.class);
        } catch (Exception ignored) {
        }

        if (excludeFromHelp) {
            return;
        }

        final String instanceHelpString = getHelpString();

        helpString = getCommandVariants().stream()
                .reduce((s1, s2) -> s1 + ", " + s2)
                .orElse("")
                + " - "
                + instanceHelpString
                + ".\n"
                + helpString;
    }

    protected Set<String> getCommandVariants() {
        return commandVariants;
    }
}
