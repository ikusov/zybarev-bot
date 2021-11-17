package ru.ikusov.training.telegrambot.services;

import org.telegram.telegrambots.meta.api.objects.User;

public class UserNameGetter {
    public static String getUserName(User user) {
        String firstName = user.getFirstName(),
                lastName = user.getLastName();

        String userName = firstName == null ? "" : (firstName + " ");
        userName += lastName == null ? "" : lastName;
        userName += userName.equals("") ? user.getUserName() : "";
        userName += userName.equals("") ? "пользователь №" + user.getId() : "";

        return userName;
    }
}
