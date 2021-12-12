package ru.ikusov.training.telegrambot.botservices;

import ru.ikusov.training.telegrambot.Bot;

/**
 * empty reaction instead of null return
 */
public class BotEmptyReaction implements BotReaction {
    @Override
    public void react(Bot bot) {

    }
}
