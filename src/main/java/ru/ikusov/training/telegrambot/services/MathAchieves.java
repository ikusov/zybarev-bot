package ru.ikusov.training.telegrambot.services;

import ru.ikusov.training.telegrambot.model.ChatEntity;
import ru.ikusov.training.telegrambot.model.ExampleAnswerEntity;
import ru.ikusov.training.telegrambot.model.UserEntity;
import ru.ikusov.training.telegrambot.utils.Linguistic;
import ru.ikusov.training.telegrambot.utils.MyMath;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import static ru.ikusov.training.telegrambot.utils.MyMath.r;

public class MathAchieves {
    private final List<MathAchieve> achieveList = new ArrayList<>();
    private final MathAchieves achieves = this;

    private int number;
    private int solvedExamplesCount;
    private int rightAnswersSeries;
    private int globalSeries;

    public int getScore() {
        return score;
    }

    private int score;
    private final int timer;

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

        getAllAchieves();
    }

    public MathAchieves(DatabaseConnector databaseConnector, UserEntity user, ChatEntity chat, int number, int timer) {
        this.number = number;
        this.timer = timer;

        getFromDataBase(databaseConnector, user, chat);
        getAllAchieves();
    }

    public int getSum() {
        return achieveList.stream().mapToInt(MathAchieve::getBonus).sum()+1;
    }

    private void getAllAchieves() {
        getRandomAchieves();
        getUserAchieves();
        getSpeedBonus();
        getNumberAchieves();
        //commented until clarification
//        getBonusAchieves();
    }

    public List<MathAchieve> getAchieveList() {
        return achieveList;
    }

    private void getFromDataBase(DatabaseConnector databaseConnector, UserEntity user, ChatEntity chat) {
        String query = "from ExampleAnswerEntity";
        var userId = user.getId();
        var chatId = chat.getId();
        List<ExampleAnswerEntity> answers = databaseConnector.getByQuery(ExampleAnswerEntity.class, query);
        answers.sort(Comparator.reverseOrder());

        solvedExamplesCount = (int) answers.stream()
                .filter(a -> a.getChat().getId() == chatId)
                .filter(a -> a.getUser().getId() == userId)
                .filter(ExampleAnswerEntity::isRight)
                .count();

        globalSeries = (int) answers.stream()
                .filter(a -> a.getChat().getId() == chatId)
                .takeWhile(a -> a.getUser().getId() == userId || !a.isRight())
                .filter(a -> a.getUser().getId() == userId && a.isRight())
                .count();

        rightAnswersSeries = (int) answers.stream()
                .filter(a -> a.getChat().getId() == chatId)
                .filter(a -> a.getUser().getId() == userId)
                .takeWhile(ExampleAnswerEntity::isRight)
                .count();

        score = answers.stream()
                .filter(a -> a.getChat().getId() == chatId)
                .filter(a -> a.getUser().getId() == userId)
                .filter(ExampleAnswerEntity::isRight)
                .mapToInt(ExampleAnswerEntity::getScore)
                .sum();

    }

    public String getTimeMessage(String userNaming) {
        String msg = "";

        if (timer<=5)
            msg = "fast as diarrhea!";
        if (timer==6)
            msg = "человек-молния!";
        if (timer>6 && timer<10)
            msg = "подобен стремительному ветру!";
        if (timer>9 && timer<16)
            msg = "подобен мудрой черепахе!";
        if (timer>15 && timer<26)
            msg = "подобен неспешной коале!";
        if (timer>25 && timer<36)
            msg = "подобен ленивцу!";
        if (timer>35 && timer<46)
            msg = "подобен эстонскому ленивцу!";
        if (timer>45 && timer<56)
            msg = "подобен эстонскому ленивцу под транквилизаторами!";
        if (timer>55)
            msg = "подобен старой бабУшке!";

        return String.format("(%s) %s - %s\n", MyMath.secondsToReadableTime(timer), userNaming, msg);
    }

    private void getSpeedBonus() {
        int speedBonus = 11;
        int length = String.valueOf(Math.abs(number)).length();
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
        int numberAbs = Math.abs(number);
        if (number < 0)
            achieves.put(1, "signed int");
        if (String.valueOf(numberAbs).length() == 3)
            achieves.put(2, "Трёхзнак!");
        if (String.valueOf(numberAbs).length() == 4)
            achieves.put(4, "Четырёхзнак!");
        if (String.valueOf(numberAbs).length() == 5)
            achieves.put(5, "Ябать, пять знаков!");
        if (numberAbs == 0)
            achieves.put(5, "Sweet zero!");
        if (numberAbs == 1)
            achieves.put(5, "За бога Одина!");
        if (numberAbs == 3)
            achieves.put(5, "Троица!");
        if (numberAbs == 5)
            achieves.put(5, "Пять за пять!");
        if (numberAbs == 7)
            achieves.put(5, "Lucky se7en!");
        if (numberAbs == 11)
            achieves.put(5, "Барабанные палочки!");
        if (numberAbs == 13)
            achieves.put(5, "Чёртова дюжина!");
        if (numberAbs == 21)
            achieves.put(5, "Очко!");
        if (numberAbs == 41)
            achieves.put(5, "41 ем один!");
        if (numberAbs == 42)
            achieves.put(5, "42 = Главный ответ!");
        if (numberAbs == 45)
            achieves.put(5, "45 - баба ягодка опять!");
        if (numberAbs == 48)
            achieves.put(5, "48 - половинку просим!");
        if (numberAbs == 50)
            achieves.put(5, "Полста!");
        if (numberAbs == 77)
            achieves.put(5, "Топорики!");
        if (numberAbs == 88)
            achieves.put(5, "Бабушка!");
        if (numberAbs == 89)
            achieves.put(5, "Дедушкин сосед!");
        if (numberAbs == 90)
            achieves.put(5, "Дедушка!");
        if (numberAbs == 99)
            achieves.put(5, "Шарики!");
        if (numberAbs == 100)
            achieves.put(5, "Соточка!");
        if (numberAbs == 220)
            achieves.put(5, "Электромэн 220 вольт!");
        if (numberAbs == 666)
            achieves.put(10, "Ацкей сотона!");
        if (numberAbs == 1983)
            achieves.put(10, "Год рождения создателя!");
        if (numberAbs%100 == 99)
            achieves.put(5, "Бонус ЭЛЬДОРАДО!");
        if (numberAbs%100 == 69)
            achieves.put(5, "69... Ух! Проказник!");
        if (numberAbs == 486)
            achieves.put(1, "x86 - бонус компьютерной индустрии!");
        if (numberAbs == 386)
            achieves.put(2, "x86 - бонус компьютерной индустрии!");
        if (numberAbs == 286)
            achieves.put(5, "x86 - бонус компьютерной индустрии!");
        if (numberAbs == 8086)
            achieves.put(10, "8086 - бонус компьютерной индустрии! Мы помним!");
        if (numberAbs == 4004)
            achieves.put(10, "4004 - бонус компьютерной индустрии! Мы помним!");
        if (numberAbs>0 && numberAbs%100 == 0)
            achieves.put(5, "За ровный счёт! Кратно 100.");
        if (numberAbs>0 && numberAbs%1000 == 0)
            achieves.put(10, "За ровный счёт! Кратно 1000.");
        if (ladderLength > 0)
            achieves.put(ladderLength, String.format("Лесенка! Длина %d.", ladderLength));
        if (MyMath.isPrime(numberAbs))
            achieves.put((int)Math.log(numberAbs), "За простое число!");
        if (MyMath.isFromOneDigit(numberAbs))
            achieves.put(5, "Из одной цифры!");
        if (MyMath.palindromeLength(numberAbs)>0)
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

    public static class MathAchieve {
        private final int bonus;
        private final String message;

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
