package ru.ikusov.training.telegrambot.utils;

import static ru.ikusov.training.telegrambot.utils.MyMath.r;

public final class RandomMessageGenerator {
    public enum MessageType {
        GREETING_MESSAGE,
        UNKNOWN_COMMAND_MESSAGE,
        RIGHT_ANSWER_MESSAGE,
        WRONG_ANSWER_MESSAGE,
        GOOD_EXAMPLE_ANSWER_MESSAGE,
        PAINTING_QUERY
    }

    private static final String[] GREETINGS = {"Я вас категорически приветствую, %s! Зыбарев бот.",
                                        "Всех благ тебе, достопочтенный %s! Зыбарев бот.",
                                        "Здарова %s! Какие дела? Зыбарев бот.",
                                        "Моё почтение, о дражайший %s! Ваш покорный слуга Зыбарев бот.",
                                        "Приветствие моё ты, %s, прочесть можешь сейчас. Дроид Зыбарев на связи с тобой."};

    private static final String[] UNKNOWNS = {"Команда %s на данный момент не поддерживается. По вопросам поддержки обращайтесь к разрабочику.",
                                                "Неизвестная команда %s.",
                                                "Я пока что не умею обрабатывать команду %s, но у меня всё впереди!",
                                                "Что ещё за %s? Не знаю такой команды. Приходите завтра."};

    private static final String[] RIGHT_ANSWERS = {"%s - правильный ответ! Молодец, %s, ответил за %s!",
                                                    "%s - совершенно верно! Гениально, %s, ты справился всего за %s! Продолжай в том же духе!",
                                                    "%s - несоменно, так! Ты точно не пользовался калькулятором, %s? Ведь ты ответил всего за %s!"
                                                    };

    private static final String[] WRONG_ANSWERS = {"%s - неправильный ответ! Подумай ещё, %s!",
                                                    "Точно %s? Попробуй ещё раз, %s!",
                                                    "%s? Серьёзно? А если подумать? %s, ты меня расстраиваешь!"};

    private static final String[] GOOD_EXAMPLE_ANSWERS = {"Рад стараться!",
                                                            "Премного благодарен! Всё для вас!",
                                                           "Любой каприз за ваши деньги!"};

    private static final String[] PAINTING_QUERIES = {"картина левитана",
                                                         "картина шишкина",
                                                        "пейзаж",
                                                          "картина васнецова"};

    private RandomMessageGenerator() {}

    public static String generate(MessageType messageType, String... args) {
        if (messageType == MessageType.GREETING_MESSAGE)
            return String.format(GREETINGS[r(GREETINGS.length)], args[0]);
        else if(messageType == MessageType.UNKNOWN_COMMAND_MESSAGE)
            return String.format(UNKNOWNS[r(UNKNOWNS.length)], args[0]);
        else if(messageType == MessageType.RIGHT_ANSWER_MESSAGE)
            return String.format(RIGHT_ANSWERS[r(RIGHT_ANSWERS.length)], args[0], args[1], args[2]);
        else if(messageType == MessageType.WRONG_ANSWER_MESSAGE)
            return String.format(WRONG_ANSWERS[r(WRONG_ANSWERS.length)], args[0], args[1]);
        else if(messageType == MessageType.GOOD_EXAMPLE_ANSWER_MESSAGE)
            return GOOD_EXAMPLE_ANSWERS[r(GOOD_EXAMPLE_ANSWERS.length)];
        else if(messageType == MessageType.PAINTING_QUERY)
            return PAINTING_QUERIES[r(PAINTING_QUERIES.length)];

        return "";
    }
}
