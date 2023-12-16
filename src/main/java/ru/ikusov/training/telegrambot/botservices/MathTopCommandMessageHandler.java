package ru.ikusov.training.telegrambot.botservices;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.ikusov.training.telegrambot.model.CommandType;
import ru.ikusov.training.telegrambot.model.MyBotCommand;
import ru.ikusov.training.telegrambot.services.MathTopGetter;
import ru.ikusov.training.telegrambot.utils.Linguistic;

@Component
@RequiredArgsConstructor
public class MathTopCommandMessageHandler extends CommandMessageHandler {
    private final MathTopGetter mathTopGetter;

    @Override
    protected CommandType getSupportedCommandType() {
        return CommandType.MATHTOP;
    }

    @Override
    public BotReaction handleCommand(MyBotCommand command) {
        String textAnswer = "";

        try {
            var mathTop = mathTopGetter.getMathTop(command.chatId());
            for (int i = 0; i < mathTop.size(); i++) {
                var mathTopEntry = mathTop.get(i);
                long score = mathTopEntry.getValue();
                textAnswer += String.format("%d. %s - %d балл%s\n",
                        i + 1,
                        mathTopEntry.getKey(),
                        score,
                        Linguistic.getManulWordEnding((int) score));
            }
        } catch (Exception e) {
            textAnswer = e.getMessage();
        }

        return new BotMessageSender(command.chatId(), command.topicId(), textAnswer);
    }

}
