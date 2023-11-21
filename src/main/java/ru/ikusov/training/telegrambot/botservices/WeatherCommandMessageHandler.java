package ru.ikusov.training.telegrambot.botservices;

import lombok.RequiredArgsConstructor;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import ru.ikusov.training.telegrambot.botservices.annotation.ExcludeFromHelp;
import ru.ikusov.training.telegrambot.model.LocationEntity;
import ru.ikusov.training.telegrambot.model.MyBotCommand;
import ru.ikusov.training.telegrambot.services.LocationDatabaseGetter;
import ru.ikusov.training.telegrambot.services.WeatherGetter;

import java.util.Set;

@Component
@Order(80)
@RequiredArgsConstructor
public class WeatherCommandMessageHandler extends CommandMessageHandler {

    private final LocationDatabaseGetter locationDatabaseGetter;

    @Override
    protected Set<String> getCommandVariants() {
        return Set.of("/weather", "/w", "/погода");
    }

    @Override
    @ExcludeFromHelp
    protected String getHelpString() {
        return "текущая погода в указанной локации. По умолчанию - Новосибирск, но это не точно";
    }

    @Override
    public BotReaction handleCommand(MyBotCommand command) {
        WeatherGetter weatherGetter;
        String textAnswer;

        String params = command.getParams();

        try {
            LocationEntity location = locationDatabaseGetter.getLocation(command.getUser(), params);
            weatherGetter = new WeatherGetter(location);
            textAnswer = weatherGetter.getWeather();
        } catch (Exception e) {
            textAnswer = e.getMessage();
        }

        return new BotMessageSender(command.getChatId(), command.getTopicId(), textAnswer);
    }
}
