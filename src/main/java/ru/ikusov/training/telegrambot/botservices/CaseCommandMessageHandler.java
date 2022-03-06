package ru.ikusov.training.telegrambot.botservices;

import com.google.common.base.CaseFormat;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import ru.ikusov.training.telegrambot.model.MyBotCommand;
import ru.ikusov.training.telegrambot.services.CaseQuestionGenerator;
import ru.ikusov.training.telegrambot.services.RhymeGetter;

import java.util.Set;

@Component
@Order(170)
public class CaseCommandMessageHandler extends CommandMessageHandler {
    private final Set<String> commandVariants = Set.of("/case", "/падеж");

    @Autowired
    private CaseQuestionGenerator caseQuestionGenerator;

    @Override
    protected Set<String> getCommandVariants() {
        return commandVariants;
    }

    @Override
    protected void addHelp() {
        String help = commandVariants.stream().reduce((s1, s2) -> s1 + ", " + s2).orElse("");
        help += " - вопросу по падежей русскому языком.\n";
        helpString = help + helpString;
    }

    @Override
    public BotReaction handleCommand(MyBotCommand command) {
        String textAnswer;

        try {
            textAnswer = caseQuestionGenerator.getQuestion();
        } catch (Exception e) {
            textAnswer = e.getMessage();
        }

        return new BotMessageSender(command.getChatId(), textAnswer);
    }
}
