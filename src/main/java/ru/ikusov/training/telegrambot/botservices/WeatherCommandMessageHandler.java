package ru.ikusov.training.telegrambot.botservices;

import lombok.RequiredArgsConstructor;
import ru.ikusov.training.telegrambot.model.CommandType;
import ru.ikusov.training.telegrambot.model.LocationEntity;
import ru.ikusov.training.telegrambot.model.MyBotCommand;
import ru.ikusov.training.telegrambot.services.LocationDatabaseGetter;
import ru.ikusov.training.telegrambot.services.WeatherGetter;

//@Component
@RequiredArgsConstructor
public class WeatherCommandMessageHandler extends CommandMessageHandler {

    private final LocationDatabaseGetter locationDatabaseGetter;

    @Override
    protected CommandType getSupportedCommandType() {
        return CommandType.WEATHER;
    }

    @Override
    public BotReaction handleCommand(MyBotCommand command) {
        WeatherGetter weatherGetter;
        String textAnswer;

        String params = command.params();

        try {
            LocationEntity location = locationDatabaseGetter.getLocation(command.user(), params);
            weatherGetter = new WeatherGetter(location);
            textAnswer = weatherGetter.getWeather();
        } catch (Exception e) {
            textAnswer = e.getMessage();
        }

        return new BotMessageSender(command.chatId(), command.topicId(), textAnswer);
    }
}
