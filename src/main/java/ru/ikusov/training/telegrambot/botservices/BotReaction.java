package ru.ikusov.training.telegrambot.botservices;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.ikusov.training.telegrambot.Bot;

/**
 * interface for different bot reactions
 */
public interface BotReaction {
    Logger log = LoggerFactory.getLogger(BotReaction.class);

    //public abstract just for fun
    public abstract void react(Bot bot);

    default void log() {
        log.info("REACTION ReactionType: '{}'", this.getClass().getSimpleName());
    }
}
