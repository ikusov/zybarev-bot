package ru.ikusov.training.telegrambot.model;

import junit.framework.TestCase;

public class UserEntityTest extends TestCase {

    public void testTestToString() {
        UserEntity user1 = new UserEntity().setId(1234).setFirstName("Duduk");
        UserEntity user2 = new UserEntity().setId(2323).setFirstName("Kukuk").setLastName("Bubukov");
        UserEntity user3 = new UserEntity().setId(2323).setFirstName("Bubukt").setLastName("Uhahakov").setUserName("superuser");

        System.out.println("user1 = " + user1);
        System.out.println("user2 = " + user2);
        System.out.println("user3 = " + user3);
    }
}