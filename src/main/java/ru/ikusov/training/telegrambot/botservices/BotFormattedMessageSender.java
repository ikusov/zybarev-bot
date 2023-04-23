package ru.ikusov.training.telegrambot.botservices;

import ru.ikusov.training.telegrambot.utils.MyString;

/**
 * class for sending formatted (markdownv2) message to chat
 * may be excess and its functionality could be added to BotMessageSender
 */
public class BotFormattedMessageSender extends BotMessageSender {
    public BotFormattedMessageSender(String chatId, Integer topicId, String textMessage) {
        super(chatId, topicId, textMessage);
        this.sendMessage.setParseMode("MarkdownV2");
    }

    @Override
    public void log() {
        log.info("REACTION ChatId: '{}' TopicId: '{}' FormattedMessage: {}",
                chatId, topicId, MyString.unmarkdownv2Format(textMessage));
    }
}
