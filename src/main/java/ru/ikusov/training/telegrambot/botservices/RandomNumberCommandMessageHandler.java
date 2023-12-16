package ru.ikusov.training.telegrambot.botservices;

import org.springframework.stereotype.Component;
import ru.ikusov.training.telegrambot.model.CommandType;
import ru.ikusov.training.telegrambot.model.MyBotCommand;

import static ru.ikusov.training.telegrambot.utils.MyMath.r;

@Component
public class RandomNumberCommandMessageHandler extends CommandMessageHandler {
    private static final int DEFAULT_TO = 100;

    private static final String ERROR1_MESSAGE = "Не могу понять, что за число %s.";

    @Override
    protected CommandType getSupportedCommandType() {
        return CommandType.RANDOM;
    }

    @Override
    public BotReaction handleCommand(MyBotCommand command) {
        String paramsString = command.params();
        String textAnswer = calculate(paramsString);

        return new BotMessageSender(command.chatId(), command.topicId(), textAnswer);
    }

    private String calculate(String paramsString) {
        int from, to;
        if (paramsString.equals("")) {
            return String.valueOf(r(DEFAULT_TO));
        }

        String[] params = paramsString.split(" ");

        if (params.length == 1) try {
            to = Integer.parseInt(params[0]);
            return String.valueOf(r(to));
        } catch (NumberFormatException e) {
            return String.format(ERROR1_MESSAGE, params[0]);
        }

        try {
            from = Integer.parseInt(params[0]);
        } catch (NumberFormatException e) {
            return String.format(ERROR1_MESSAGE, params[0]);
        }
        try {
            to = Integer.parseInt(params[1]);
        } catch (NumberFormatException e) {
            return String.format(ERROR1_MESSAGE, params[1]);
        }

        return String.valueOf(from + r(to - from));
    }
}
