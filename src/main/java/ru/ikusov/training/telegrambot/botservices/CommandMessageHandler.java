package ru.ikusov.training.telegrambot.botservices;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Message;
import ru.ikusov.training.telegrambot.Bot;
import ru.ikusov.training.telegrambot.model.MyBotCommand;
import ru.ikusov.training.telegrambot.utils.RandomMessageGenerator;

import java.util.HashSet;
import java.util.Set;

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

    protected static Set<String> registeredCommands = new HashSet<>();

    @Override
    public void handleMessage(Message message) {
//        System.out.println("CommandMessageHandler.handleMessage()!");

        String msgText = message.getText();

        if (msgText.charAt(0) != '/') return;

        registerCommands();

        String[] tokens = msgText.split(" ", 2);

        String[] commandParts = tokens[0].split("@");

        if (commandParts.length > 1 && !commandParts[1].equals(bot.getBotUsername())) {
            return;
        }

        tokens[0] = commandParts[0];
        command = new MyBotCommand(tokens[0], tokens.length>1 ? tokens[1] : "", message.getChatId().toString(), message.getFrom());

        Set<String> commandVariants = getCommandVariants();

        if (commandVariants.isEmpty() || commandVariants.contains(command.getCommand().toLowerCase())) {
            BotReaction botReaction = handleCommand(command);
            if (botReaction != null)
                botReaction.react(bot);
        }
    }

    protected void registerCommands() {
        registeredCommands.addAll(getCommandVariants());
    }

//todo: add method to describe every command handler for /help command

    protected abstract Set<String> getCommandVariants();

    public abstract BotReaction handleCommand(MyBotCommand command);
}
