package de.notion.messaging.redis;

import de.notion.messaging.message.Message;
import de.notion.messaging.message.MessageBuilder;

import java.util.UUID;

public class RedisMessageBuilder extends MessageBuilder {
    public RedisMessageBuilder(UUID sender, String senderIdentifier) {
        super(sender, senderIdentifier);
    }

    @Override
    public Message build() {
        return new SimpleRedisMessage(sender, senderIdentifier, parameters, dataToSend);
    }
}
