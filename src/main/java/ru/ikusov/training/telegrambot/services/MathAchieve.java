package ru.ikusov.training.telegrambot.services;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

public class MathAchieve {
    private Map<Integer, String> achieves = new HashMap<>();


    private MathAchieve() {
    }

    public void getNumberAchieves(int number) {
        if (number < 0)
            achieves.put(1, "signed int");
        if (String.valueOf(number).length() == 3)
            achieves.put(2, "Трёхзнак!");
        if (String.valueOf(number).length() == 4)
            achieves.put(4, "Четырёхзнак!");
        if (String.valueOf(number).length() == 5)
            achieves.put(5, "Ябать, пять знаков!");
        if (number == 0)
            achieves.put(5, "Sweet zero!");
        if (number == 1)
            achieves.put(5, "За бога Одина!");
        if (number == 3)
            achieves.put(5, "Троица!");
        if (number == 5)
            achieves.put(5, "Пять за пять!");
        if (number == 7)
            achieves.put(5, "Lucky se7en!");
        if (number == 11)
            achieves.put(5, "Барабанные палочки!");
        if (number == 13)
            achieves.put(5, "Чёртова дюжина!");
        if (number == 21)
            achieves.put(5, "Очко!");
        if (number == 41)
            achieves.put(5, "41 ем один!");
        if (number == 42)
            achieves.put(5, "42 = Главный ответ!");
        if (number == 45)
            achieves.put(5, "45 - баба ягодка опять!");
        if (number == 48)
            achieves.put(5, "48 - половинку просим!");
        if (number == 50)
            achieves.put(5, "Полста!");
        if (number == 77)
            achieves.put(5, "Топорики!");
        if (number == 88)
            achieves.put(5, "Бабушка!");
        if (number == 89)
            achieves.put(5, "Дедушкин сосед!");
        if (number == 90)
            achieves.put(5, "Дедушка!");
        if (number == 99)
            achieves.put(5, "Шарики!");
        if (number == 100)
            achieves.put(5, "Соточка!");
        if (number == 220)
            achieves.put(5, "Электромэн 220 вольт!");
        if (number == 666)
            achieves.put(10, "Ацкей сотона!");
        if (number == 1983)
            achieves.put(10, "Год рождения создателя!");
        if (number%100 == 99)
            achieves.put(5, "Бонус ЭЛЬДОРАДО!");
        if (number%100 == 69)
            achieves.put(5, "69... Ух! Проказник!");
        if (number == 486)
            achieves.put(1, "x86 - бонус компьютерной индустрии!");
        if (number == 386)
            achieves.put(2, "x86 - бонус компьютерной индустрии!");
        if (number == 286)
            achieves.put(5, "x86 - бонус компьютерной индустрии!");
        if (number == 8086)
            achieves.put(10, "8086 - бонус компьютерной индустрии! Мы помним!");
        if (number == 4004)
            achieves.put(10, "4004 - бонус компьютерной индустрии! Мы помним!");
    }

}
