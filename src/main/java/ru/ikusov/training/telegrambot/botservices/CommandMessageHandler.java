package ru.ikusov.training.telegrambot.botservices;

import org.springframework.beans.factory.annotation.Autowired;
import org.telegram.telegrambots.meta.api.objects.Message;
import ru.ikusov.training.telegrambot.Bot;
import ru.ikusov.training.telegrambot.model.MyBotCommand;

import java.util.HashSet;
import java.util.Set;

/**
 * abstract class for command-type (starting from '/') messages handlers
 * contains shared logics, such as checking if message is command, splitting message to
 * command and params etc.
 */

public abstract class CommandMessageHandler implements MessageHandler {
    private Bot bot;

    @Autowired
    public void setBot(Bot bot) {
        this.bot = bot;
    }

    public CommandMessageHandler() {
//        System.out.println(this.getClass().getSimpleName() + " created!");
    }

    private MyBotCommand command;

    //static collection of registered commands
    protected static Set<String> registeredCommands = new HashSet<>();

    @Override
    public void handleMessage(Message message) {
        String msgText = message.getText();

        //if message is not command
        if (msgText.charAt(0) != '/') return;

        //add command handler commands to registry
        registerCommands();

        //tokens of message: /command token1 token2 ... token2_000_047 ...
        String[] tokens = msgText.split(" ", 2);

        //parts of command: /command@bot@dunnowhat@itsridiculous
        String[] commandParts = tokens[0].split("@");

        //if for all that there is some ridiculous in the command
        if (commandParts.length > 1 && !commandParts[1].equals(bot.getBotUsername())) {
            return;
        }

        //use the same variable for MyBotCommand instance construction
        tokens[0] = commandParts[0].toLowerCase();
        command = new MyBotCommand(tokens[0], tokens.length>1 ? tokens[1] : "", message.getChat(), message.getFrom());

        //every descendant has its own command variants
        Set<String> commandVariants = getCommandVariants();

        //if no variants - that's for unknown command handler -
        //or
        //if the descendant instance's command variants contains the command
        if (commandVariants.isEmpty() || commandVariants.contains(command.getCommand())) {
            BotReaction botReaction = handleCommand(command);
            botReaction.react(bot);
        }
    }

    protected void registerCommands() {
        registeredCommands.addAll(getCommandVariants());
    }

//todo: add method to describe every command handler for /help command

    protected abstract Set<String> getCommandVariants();

    //the method to be implemented by descendants
    public abstract BotReaction handleCommand(MyBotCommand command);
}
