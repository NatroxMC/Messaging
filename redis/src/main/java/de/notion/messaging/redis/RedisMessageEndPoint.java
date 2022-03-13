package de.notion.messaging.redis;

import de.notion.messaging.MessagingService;
import de.notion.messaging.channel.MessageChannel;
import de.notion.messaging.message.Message;
import de.notion.messaging.message.MessageBuilder;
import de.notion.messaging.sender.MessageEndPoint;
import org.jetbrains.annotations.NotNull;
import org.redisson.api.RedissonClient;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class RedisMessageEndPoint implements MessageEndPoint {

    private final MessagingService messagingService;
    private final RedissonClient redissonClient;
    private final Map<String, MessageChannel> cache;
    private final UUID sessionUUID;

    public RedisMessageEndPoint(MessagingService messagingService, RedissonClient redissonClient) {
        this.messagingService = messagingService;
        this.redissonClient = redissonClient;
        this.cache = new HashMap<>();
        this.sessionUUID = UUID.randomUUID();
    }

    @NotNull
    @Override
    public MessageBuilder messageBuilder() {
        return new MessageBuilder(sessionUUID(), "");
    }

    @Override
    public MessageChannel channel(String name) {
        var channelName = "MessagingChannel_" + name;
        if (!cache.containsKey(channelName))
            cache.put(channelName, new RedisMessageChannel(channelName, this));
        return cache.get(channelName);
    }

    @Override
    public boolean isOwnMessage(Message message) {
        return message.sender().equals(sessionUUID());
    }

    @NotNull
    @Override
    public UUID sessionUUID() {
        return sessionUUID;
    }

    public RedissonClient redissonClient() {
        return this.redissonClient;
    }
}
