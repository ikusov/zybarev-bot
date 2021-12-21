package ru.ikusov.training.telegrambot.botservices;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import ru.ikusov.training.telegrambot.model.LocationEntity;
import ru.ikusov.training.telegrambot.model.MyBotCommand;
import ru.ikusov.training.telegrambot.model.UserEntity;
import ru.ikusov.training.telegrambot.services.DatabaseConnector;
import ru.ikusov.training.telegrambot.services.LocationDatabaseGetter;
import ru.ikusov.training.telegrambot.services.WeatherGetter;
import ru.ikusov.training.telegrambot.services.WeatherGetter2;
import ru.ikusov.training.telegrambot.utils.MyString;

import javax.xml.crypto.Data;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.Set;

@Component
@Order(80)
public class WeatherCommandMessageHandler extends CommandMessageHandler {
    private final Set<String> commandVariants = Set.of("/weather", "/w", "/погода");

    @Autowired
    LocationDatabaseGetter locationDatabaseGetter;

    @Override
    protected void addHelp() {
        String help = commandVariants.stream().reduce((s1, s2) -> s1 + ", " + s2).orElse("");
        help += " - текущая погода в указанной локации. По умолчанию - Новосибирск, но это не точно.\n";
        helpString = help + helpString;
    }


    @Override
    protected Set<String> getCommandVariants() {
        return commandVariants;
    }

    @Override
    public BotReaction handleCommand(MyBotCommand command) {
        WeatherGetter2 weatherGetter;
        String textAnswer;

        String params = command.getParams();

        LocationEntity location = locationDatabaseGetter.getLocation(command.getUser(), params);

        try {
            weatherGetter = new WeatherGetter2(location);
            textAnswer = weatherGetter.getWeather();
        } catch (Exception e) {
            textAnswer = e.getMessage();
        }

        return new BotMessageSender(command.getChatId(), textAnswer);
    }
}
