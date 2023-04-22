package ru.ikusov.training.telegrambot.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.User;
import ru.ikusov.training.telegrambot.dao.DatabaseConnector;
import ru.ikusov.training.telegrambot.model.LocationEntity;
import ru.ikusov.training.telegrambot.model.UserEntity;
import ru.ikusov.training.telegrambot.utils.MyString;

import java.util.Arrays;
import java.util.List;
import java.util.Locale;

@Component
public class LocationDatabaseGetter {
    @Autowired
    private DatabaseConnector databaseConnector;

    public LocationDatabaseGetter() {
    }

    public LocationEntity getLocation(User chatUser, String params) throws IllegalArgumentException {
        LocationEntity location = LocationEntity.DEFAULT_LOCATION;

        //if no parameters, try to get user location from da tabase
        if (params.equals("")) {
            var userEntityOptional = databaseConnector.getById(UserEntity.class, chatUser.getId());
            if (userEntityOptional.isPresent() && userEntityOptional.get().getLocation()!=null) {
                location = userEntityOptional.get().getLocation();
            }
        } else {        //if have parameters, try to find location in da tabase
            String locationRequest = MyString.trimPunctuationMarksInclusive(params)
                    .toLowerCase(Locale.ROOT).replaceAll(" ", "");

            //get all locations from da tabase
            List<LocationEntity> locations =
                    databaseConnector.getByQueryNotEmpty(LocationEntity.class, "from LocationEntity");

            //if one of locations has alias equals to command param, get the location
            boolean noAliasesFound = true;
            for (LocationEntity locationEntity : locations) {
                List<String> aliases = Arrays
                        .asList(locationEntity.getAliases().toLowerCase(Locale.ROOT).split(";"));
                if (aliases.contains(locationRequest)) {
                    location = locationEntity;
                    noAliasesFound = false;
                    break;
                }
            }

            //if not found, try to get from yandex
            if (noAliasesFound) {
                try {
                    location = new GeocodeGetter(locationRequest).getGeoCode();
                    databaseConnector.save(location);
                } catch (IllegalArgumentException e) {
                    throw new IllegalArgumentException("Не найдена локация " + params);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        return location;
    }
}
