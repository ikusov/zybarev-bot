package ru.ikusov.training.telegrambot.botservices;

import org.springframework.beans.factory.annotation.Autowired;
import ru.ikusov.training.telegrambot.Bot;

public interface BotReaction {
    public abstract void react(Bot bot);
}
