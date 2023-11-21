package ru.ikusov.training.telegrambot.botservices;

import lombok.RequiredArgsConstructor;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import ru.ikusov.training.telegrambot.botservices.annotation.ExcludeFromHelp;
import ru.ikusov.training.telegrambot.model.MyBotCommand;
import ru.ikusov.training.telegrambot.services.MathTopGetter;
import ru.ikusov.training.telegrambot.utils.Linguistic;

import java.util.Set;

@Component
@Order(150)
@RequiredArgsConstructor
public class MathTopCommandMessageHandler extends CommandMessageHandler {

    private final MathTopGetter mathTopGetter;

    @Override
    protected Set<String> getCommandVariants() {
        return Set.of("/top", "/топ");
    }

    @Override
    @ExcludeFromHelp
    protected String getHelpString() {
        return "топ математических баллов";
    }

    @Override
    public BotReaction handleCommand(MyBotCommand command) {
        String textAnswer = "";

        try {
            var mathTop = mathTopGetter.getMathTop(command.getChatId());
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

        return new BotMessageSender(command.getChatId(), command.getTopicId(), textAnswer);
    }

}
