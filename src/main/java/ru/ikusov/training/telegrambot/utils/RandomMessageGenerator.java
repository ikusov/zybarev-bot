package ru.ikusov.training.telegrambot.utils;

import java.util.List;

import static ru.ikusov.training.telegrambot.utils.MyMath.r;

// The class is not in use, its functionality moved to MessageType enum.
// Saved for history.
public final class RandomMessageGenerator {

    public enum MessageType {
        GREETING_MESSAGE(List.of(
                "Счастья тебе в новом году, %s! Зыбарев - новогодний бот. \uD83D\uDF84",
                "Всех благ тебе в Новом году, достопочтенный %s! Зыбарев - новогодний бот. \uD83D\uDF84",
                "Здарова %s! С Новым годом! Зыбарев - праздничный бот. \uD83D\uDF84",
                "Поздравление моё ты, %s, прочесть можешь сейчас. Новогодний дроид Зыбарев на связи с тобой. \uD83D\uDF84"
        )),
        UNKNOWN_COMMAND_MESSAGE(List.of(
                "Команда %s на данный момент не поддерживается. По вопросам поддержки обращайтесь к разработчику.",
                "Неизвестная команда %s.",
                "Я пока что не умею обрабатывать команду %s, но у меня всё впереди!",
                "Что ещё за %s? Не знаю такой команды. Приходите завтра."
        )),
        RIGHT_ANSWER_MESSAGE(List.of(
                "%s - правильный ответ! Молодец, %s, ответил за %s!",
                "%s - совершенно верно! Гениально, %s, ты справился всего за %s! Продолжай в том же духе!",
                "%s - несоменно, так! Ты точно не пользовался калькулятором, %s? Ведь ты ответил всего за %s!"
        )),
        WRONG_ANSWER_MESSAGE(List.of(
                "%s - неправильный ответ! Подумай ещё, %s!",
                "Точно %s? Попробуй ещё раз, %s!",
                "%s? Серьёзно? А если подумать? %s, ты меня расстраиваешь!"
        )),
        GOOD_EXAMPLE_ANSWER_MESSAGE(List.of(
                "Рад стараться!",
                "Премного благодарен! Всё для вас!",
                "Спасибо, я стараюсь!"
        )),
        PAINTING_QUERY(List.of(
                "картина левитана",
                "картина шишкина",
                "пейзаж",
                "картина васнецова"
        ));

        private final List<String> messageList;

        MessageType(List<String> messageList) {
            this.messageList = messageList;
        }

        public String getRandomMessage() {
            return messageList.get(r(messageList.size()));
        }
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
                                                           "Спасибо, я стараюсь!"};

    private static final String[] PAINTING_QUERIES = {"картина левитана",
                                                         "картина шишкина",
                                                        "пейзаж",
                                                          "картина васнецова"};

    private RandomMessageGenerator() {}

    public static String generate(MessageType messageType, String... args) {
//        if (messageType == MessageType.GREETING_MESSAGE)
//            return String.format(GREETINGS[r(GREETINGS.length)], args[0]);
//        else if(messageType == MessageType.UNKNOWN_COMMAND_MESSAGE)
//            return String.format(UNKNOWNS[r(UNKNOWNS.length)], args[0]);
//        else if(messageType == MessageType.RIGHT_ANSWER_MESSAGE)
//            return String.format(RIGHT_ANSWERS[r(RIGHT_ANSWERS.length)], args[0], args[1], args[2]);
//        else if(messageType == MessageType.WRONG_ANSWER_MESSAGE)
//            return String.format(WRONG_ANSWERS[r(WRONG_ANSWERS.length)], args[0], args[1]);
//        else if(messageType == MessageType.GOOD_EXAMPLE_ANSWER_MESSAGE)
//            return GOOD_EXAMPLE_ANSWERS[r(GOOD_EXAMPLE_ANSWERS.length)];
//        else if(messageType == MessageType.PAINTING_QUERY)
//            return PAINTING_QUERIES[r(PAINTING_QUERIES.length)];

        //todo: ПРОТЕСТИРОВАТЬ!!!
        return String.format(messageType.getRandomMessage(), (Object[]) args);
    }
}
