package ru.ikusov.training.telegrambot.botservices;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import ru.ikusov.training.telegrambot.model.LocationEntity;
import ru.ikusov.training.telegrambot.model.MyBotCommand;
import ru.ikusov.training.telegrambot.model.UserEntity;
import ru.ikusov.training.telegrambot.services.DatabaseConnector;
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
    DatabaseConnector databaseConnector;

    @Override
    protected Set<String> getCommandVariants() {
        return commandVariants;
    }

    @Override
    public BotReaction handleCommand(MyBotCommand command) {
//        if (!commandVariants.contains(command.getCommand().toLowerCase())) return null;
//
        WeatherGetter2 weatherGetter;
        String textAnswer;

        //default location: very cold place
        LocationEntity location = new LocationEntity().setLatitude(55.0411).setLongitude(82.9344);

        String params = command.getParams();

        if (params.equals("")) {
            UserEntity user = databaseConnector.getById(UserEntity.class, command.getUser().getId());
            if (user!=null && user.getLocation()!=null) {
                location = user.getLocation();
            }
        } else {
            String locationRequest = MyString.trimPunctuationMarksInclusive(params)
                    .toLowerCase(Locale.ROOT).replaceAll(" ", "");

            //get all locations from da tabase
            List<LocationEntity> locations =
                    databaseConnector.getByQuery(LocationEntity.class, "from LocationEntity");

            //if one of locations has alias equals to command param, get the location
            for (LocationEntity locationEntity : locations) {
                List<String> aliases = Arrays.asList(locationEntity.getAliases().toLowerCase(Locale.ROOT).split(";"));
                if (aliases.contains(locationRequest)) {
                    location = locationEntity;
                    break;
                }
            }
        }

        try {
            weatherGetter = new WeatherGetter2(location);
            textAnswer = weatherGetter.getWeather();
        } catch (Exception e) {
            textAnswer = e.getMessage();
        }

        return new BotMessageSender(command.getChatId(), textAnswer);
    }
}
