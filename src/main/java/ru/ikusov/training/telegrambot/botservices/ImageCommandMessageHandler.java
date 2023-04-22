package ru.ikusov.training.telegrambot.botservices;

import ru.ikusov.training.telegrambot.model.MyBotCommand;
import ru.ikusov.training.telegrambot.services.ImageGetter;

import java.util.Set;

//the handler is not needed, so disable injection
//saved source for history
//@Component
//@Order(130)
public class ImageCommandMessageHandler extends CommandMessageHandler {
    private final Set<String> commandVariants =
            Set.of("/picture", "/картинка", "/pic", "/пик", "/пикча");

    @Override
    protected Set<String> getCommandVariants() {
        return commandVariants;
    }

    @Override
    protected void addHelp() {
        String help = commandVariants.stream().reduce((s1, s2) -> s1 + ", " + s2).orElse("");
        help += " - случайная картинка из выдачи Яндекса.\n";
        helpString = help + helpString;
    }

    @Override
    public BotReaction handleCommand(MyBotCommand command) {
        if (!commandVariants.contains(command.getCommand().toLowerCase())) return null;
        String searchText = command.getParams();

        String photoAnswer;

        try {
            ImageGetter imageGetter = new ImageGetter(searchText);
            photoAnswer = imageGetter.getImage();
        } catch (Exception e) {
            photoAnswer = e.getMessage();
            return new BotMessageSender(command.getChatId(), photoAnswer);
        }

        return new BotPhotoSender(command.getChatId(), photoAnswer);
    }

}
