package ru.ikusov.training.telegrambot.botservices;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import ru.ikusov.training.telegrambot.model.MyBotCommand;

import java.util.Set;

import static ru.ikusov.training.telegrambot.utils.MyMath.r;

@Component
@Order(60)
public class RandomNumberCommandMessageHandler extends CommandMessageHandler {
    private final int DEFAULT_TO = 100;

    private final String ERROR1_MESSAGE = "Не могу понять, что за число %s.";

    private final Set<String> commandVariants = Set.of("/random", "/случ", "/r");

    @Override
    protected Set<String> getCommandVariants() {
        return commandVariants;
    }

    @Override
    protected void addHelp() {
        String help = commandVariants.stream().reduce((s1, s2) -> s1 + ", " + s2).orElse("");
        help += " - Случайное число. Можно попробовать передавать параметры.\n";
        helpString = help + helpString;
    }

    @Override
    public BotReaction handleCommand(MyBotCommand command) {
//        if (!commandVariants.contains(command.getCommand().toLowerCase())) return null;
//
        String paramsString = command.getParams();
        String textAnswer = calculate(paramsString);

        return new BotMessageSender(command.getChatId(), textAnswer);
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

        return String.valueOf(from + r(to-from));
    }
}
