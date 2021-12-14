package ru.ikusov.training.telegrambot.services;

import ru.ikusov.training.telegrambot.utils.MyMath;

import java.util.ArrayList;
import java.util.List;

import static ru.ikusov.training.telegrambot.utils.MyMath.r;

public class MathAchieves {
    private final List<MathAchieve> achieveList = new ArrayList<>();
    private final MathAchieves achieves = this;

    private int number;
    private int solvedExamplesCount;
    private int rightAnswersSeries;
    private int globalSeries;
    private int timer;

    private final int ladderLength = MyMath.ladderLength(number);

    private void put(int bonus, String message) {
        achieveList.add(new MathAchieve(bonus, message));
    }

    public MathAchieves(int number, int solvedExamplesCount, int rightAnswersSeries, int globalSeries, int timer) {
        this.number = number;
        this.solvedExamplesCount = solvedExamplesCount;
        this.rightAnswersSeries = rightAnswersSeries;
        this.globalSeries = globalSeries;
        this.timer = timer;

        getRandomAchieves();
        getUserAchieves();
        getSpeedBonus();
        getNumberAchieves();
        getBonusAchieves();
    }

    public List<MathAchieve> getAchieveList() {
        return achieveList;
    }

    private void getSpeedBonus() {
        int speedBonus = 11;
        int length = String.valueOf(number).length();
        speedBonus += number<0 ? 4 : 0;
        speedBonus += length>1 ? 2 : 0;
        speedBonus += length>2 ? 5 : 0;
        speedBonus += length>3 ? 7 : 0;
        speedBonus = Math.max((speedBonus - timer), 0);
        if (speedBonus>0)
            achieves.put(speedBonus, "Бонус за скорость");
    }

    private void getUserAchieves() {
        if (solvedExamplesCount%100 == 0)
            achieves.put(10, String.format("Мини-юбилей! Твой решённый пример №%d!", solvedExamplesCount));
        if (solvedExamplesCount%1000 == 0)
            achieves.put(20, String.format("Юбилей! Твой решённый пример №%d!", solvedExamplesCount));
        if (solvedExamplesCount%10_000 == 0)
            achieves.put(30, String.format("ЮБИЛЕЙ! Твой решённый пример №%d!", solvedExamplesCount));
        if (solvedExamplesCount%100_000 == 0)
            achieves.put(50, String.format(">>> ЮБИЛЕЙ!!! <<< Твой решённый пример №%d!", solvedExamplesCount));

        if (rightAnswersSeries >= 3)
            achieves.put(rightAnswersSeries-2, String.format("Личная серия без ошибок! Длина %d.", rightAnswersSeries));
        if (globalSeries >= 3)
            achieves.put(globalSeries-2, String.format("Глобальная серия! Длина %d.", globalSeries));
    }

    private void getRandomAchieves() {
        if (r(10) == 1)
            achieves.put(1, "Лаки! Случайный бонус, шанс 1 из 10.");
        if (r(100) == 1)
            achieves.put(10, "Баловень судьбы! Случайный бонус, шанс 1 из 100.");
        if (r(1000) == 1)
            achieves.put(20, "Бог рандома! Случайный бонус, шанс 1 из 1000.");
        if (r(1_000_000) == 1)
            achieves.put(50, "OMG! YOU - MEGA LUCKER! Случайный бонус, шанс 1 из 1000000!");
    }

    private void getNumberAchieves() {
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
        if (number>0 && number%100 == 0)
            achieves.put(5, "За ровный счёт! Кратно 100.");
        if (number>0 && number%1000 == 0)
            achieves.put(10, "За ровный счёт! Кратно 1000.");
        if (ladderLength > 0)
            achieves.put(ladderLength, String.format("Лесенка! Длина %d.", ladderLength));
        if (MyMath.isPrime(number))
            achieves.put((int)Math.log(number), "За простое число!");
        if (MyMath.isFromOneDigit(number))
            achieves.put(5, "Из одной цифры!");
        if (MyMath.palindromeLength(number)>0)
            achieves.put(5, "Палиндром!");
    }

    private void getBonusAchieves() {
        int sum = achieves.achieveList.stream().mapToInt(MathAchieve::getBonus).sum();
        if (sum>=3)
            achieves.put(1, "Бонус-магнат! (>=3)");
        if (sum>=5)
            achieves.put(3, "Бонус-магнат! (>=5)");
        if (sum>=8)
            achieves.put(5, "Бонус-МАГНАТ! (>=8)");
        if (sum>=10)
            achieves.put(10, "БОНУС-МАГНАТ! (>=10)");
        if (sum>=20)
            achieves.put(20, ">>> БОНУС-МАГНАТ! <<< (>=20)");
    }

    private static class MathAchieve {
        int bonus;
        String message;

        private MathAchieve(int bonus, String message) {
            this.bonus = bonus;
            this.message = message;
        }

        public int getBonus() {
            return bonus;
        }

        public String getMessage() {
            return message;
        }
    }
}