package ru.ikusov.training.telegrambot.botservices;

import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import ru.ikusov.training.telegrambot.model.MyBotCommand;
import ru.ikusov.training.telegrambot.services.WeatherGetter;

import java.io.IOException;
import java.util.Set;

@Component
@Order(80)
public class WeatherCommandMessageHandler extends CommandMessageHandler {
    private final Set<String> commandVariants = Set.of("/weather", "/w", "/погода");

    @Override
    protected Set<String> getCommandVariants() {
        return commandVariants;
    }

    @Override
    public BotReaction handleCommand(MyBotCommand command) {
//        if (!commandVariants.contains(command.getCommand().toLowerCase())) return null;
//
        WeatherGetter weatherGetter;
        String textAnswer;

        try {
            weatherGetter = new WeatherGetter();
            textAnswer = weatherGetter.getWeather();
        } catch (IOException e) {
            textAnswer = e.getMessage();
        }

        return new BotMessageSender(command.getChatId(), textAnswer);
    }
}
