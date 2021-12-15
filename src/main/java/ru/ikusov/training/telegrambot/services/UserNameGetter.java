package ru.ikusov.training.telegrambot.services;

import org.telegram.telegrambots.meta.api.objects.User;
import ru.ikusov.training.telegrambot.model.UserEntity;

public class UserNameGetter {
    public static String getUserName(User user) {
        return getUserName(user.getFirstName(), user.getLastName(), user.getUserName(), user.getId());
    }
    
    public static String getUserName(UserEntity user) {
        return getUserName(user.getFirstName(), user.getLastName(), user.getUserName(), user.getId());
    }
    
    private static String getUserName(String firstName, String lastName, String userName, Long id) {
        String userNaming = firstName == null ? "" : (firstName + " ");
        userNaming += lastName == null ? "" : lastName;
        userNaming += userNaming.equals("") ? userName : "";
        userNaming += userNaming.equals("") ? "пользователь №" + id : "";

        return userNaming;
    }
}
