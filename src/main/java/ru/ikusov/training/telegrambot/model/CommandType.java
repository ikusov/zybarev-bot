package ru.ikusov.training.telegrambot.model;

import lombok.Getter;
import ru.ikusov.training.telegrambot.botservices.annotation.ExcludeFromHelp;

import java.util.Set;

@Getter
public enum CommandType {
    ADMIN("админка",
            "admin"),
    BEE("пчела",
            "bee", "пчела"),
    CASE("вопросу по падежей русскому языком",
            "case", "падеж"),
    DONATE("поддержать бота финансово",
            "donate", "спонсор"),
    EXAMPLE("арифметический пример. Разомни мозги!",
            "example", "e", "primer", "пример"),
    GREETING("приветствие от бота",
            "bot", "hello", "пт", "бот", "привет"),
    HELP("Список команд",
            "help", "h", "?", "помощь"),
    @ExcludeFromHelp
    LEAVE("сделать бота покинуть чат",
            "leave", "goout", "изыди"),
    MATHTOP("топ математических баллов",
            "mathtop", "маттоп"),
    NEWS("случайная новость из выдачи Яндекса",
            "news", "n", "новость", "н"),
    QUOTE("умная цитата из интернета или (если вам повезло с чатиком) какая-то цитата из старого IRС-канала. " +
            "Во втором случае можно передать параметром номер цитаты",
            "quote", "q", "цитата", "ц", "й"),
    RANDOM("cлучайное число. Можно попробовать передавать параметры",
            "random", "случ", "r"),
    RHYME("смищной стишок из интернета",
            "rhyme", "стишок", "стих"),
    STAT("статистика по математическим достижениям",
            "stat", "стат", "statistics", "статистика", "матстат"),
    @ExcludeFromHelp
    WEATHER("текущая погода в указанной локации. По умолчанию - Новосибирск, но это не точно",
            "weather", "w", "погода"),
    WORDLE("Игра в слова по типу Wordle. Параметром можно задать длину слова (от 4 до 8 символов), " +
            "параметр 'стат' выводит статистику по игре",
            "wordle", "words", "слова"),
    WORDLESTAT("Выводит статистику по игре Wordle",
            "wordlestat", "wordstat", "словастат"),
    WORDLETOP("Выводит топ пользователей по количеству очков в игре Wordle. " +
            "Параметр (от 3 до 100) задаёт количество элементов списка (по умолчанию 10)",
            "wordletop", "wordtop", "словатоп", "top", "топ", "топслова"),
    UNKNOWN("");
    private final String helpString;
    private final Set<String> aliases;

    CommandType(String helpString, String... aliases) {
        this.helpString = helpString;
        this.aliases = Set.of(aliases);
    }

}
