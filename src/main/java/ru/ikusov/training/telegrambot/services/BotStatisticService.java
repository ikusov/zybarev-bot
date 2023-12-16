package ru.ikusov.training.telegrambot.services;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.ikusov.training.telegrambot.dao.BotEventRepository;
import ru.ikusov.training.telegrambot.model.BotEventEntity;
import ru.ikusov.training.telegrambot.model.CommandType;
import ru.ikusov.training.telegrambot.model.MyBotCommand;

import static ru.ikusov.training.telegrambot.utils.ExtendedChatIdConverter.toExtendedChatId;

@Service
@RequiredArgsConstructor
public class BotStatisticService {
    private final BotEventRepository botEventRepository;

    public void saveCommandInvoke(MyBotCommand command) {
        String extendedChatId = toExtendedChatId(command.chatId(), command.topicId());
        Long userId = command.user().getId();
        CommandType commandType = command.commandType();

        BotEventEntity botEventEntity = BotEventEntity.builder()
                .userId(userId)
                .extendedChatId(extendedChatId)
                .command(commandType)
                .build();
        botEventRepository.save(botEventEntity);
    }
}
