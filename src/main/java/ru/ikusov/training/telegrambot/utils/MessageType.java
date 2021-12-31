package ru.ikusov.training.telegrambot.utils;

import java.util.List;

import static ru.ikusov.training.telegrambot.utils.MyMath.r;

public enum MessageType {
    GREETING_MESSAGE(List.of(
            "❄❄❄ Счастья тебе в новом году, %s! Зыбарев - новогодний бот. \uD83C\uDF84\uD83C\uDF84\uD83C\uDF84",
            "\uD83E\uDD42\uD83E\uDD42\uD83E\uDD42 Всех благ тебе в Новом году, достопочтенный %s! Зыбарев - новогодний бот. \uD83C\uDF87\uD83C\uDF87\uD83C\uDF87",
            "\uD83C\uDF7E\uD83C\uDF7E\uD83C\uDF7E Здарова %s! С Новым годом! Зыбарев - праздничный бот. \uD83C\uDF89\uD83C\uDF89\uD83C\uDF89",
            "\uD83C\uDF84\uD83C\uDF84\uD83C\uDF84 Поздравление моё ты, %s, прочесть можешь сейчас. Новогодний дроид Зыбарев на связи с тобой. ❄❄❄"
    )),
    UNKNOWN_COMMAND_MESSAGE(List.of(
            "Команда %s на данный момент не поддерживается. По вопросам поддержки обращайтесь к разрабочику.",
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
