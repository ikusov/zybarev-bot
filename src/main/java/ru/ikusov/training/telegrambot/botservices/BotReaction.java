package ru.ikusov.training.telegrambot.botservices;

import ru.ikusov.training.telegrambot.Bot;

/**
 * interface for different bot reactions
 */
public interface BotReaction {

    //public abstract just for fun
    public abstract void react(Bot bot);
}
