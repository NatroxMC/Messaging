package de.notion.messaging.redis;

import com.google.common.eventbus.EventBus;
import de.notion.messaging.MessagingService;
import de.notion.messaging.event.MessageEvent;
import de.notion.messaging.message.Message;
import org.jetbrains.annotations.NotNull;
import org.redisson.Redisson;
import org.redisson.api.RTopic;
import org.redisson.api.RedissonClient;
import org.redisson.api.listener.MessageListener;
import org.redisson.codec.SerializationCodec;
import org.redisson.config.ClusterServersConfig;
import org.redisson.config.Config;
import org.redisson.config.SingleServerConfig;

import java.util.Objects;

public class RedisMessaging implements MessagingService<RedisMessageBuilder> {

    private final String serviceName;
    private final String senderName;
    private final RedissonClient redissonClient;
    private final RTopic globalMessagingChannel;
    private final MessageListener<Message> messageListener;
    private final EventBus eventBus;

    private final boolean loaded;

    public RedisMessaging(String serviceName, String senderName, RedissonClient redissonClient) {
        Objects.requireNonNull(serviceName, "serviceName can't be null!");
        Objects.requireNonNull(senderName, "senderName can't be null!");

        this.serviceName = serviceName;
        this.senderName = senderName;
        this.redissonClient = redissonClient;
        this.eventBus = new EventBus();

        globalMessagingChannel = redissonClient.getTopic("GlobalMessagingChannel", new SerializationCodec());

        this.messageListener = (channel, msg) -> {
            if (!(msg instanceof SimpleRedisMessage))
                return;
            // Own Messages won't throw an event
            if (isOwnMessage(msg))
                return;
            eventBus.post(new MessageEvent(channel.toString(), msg));
        };
        globalMessagingChannel.addListener(Message.class, messageListener);

        loaded = true;
    }

    @Override
    public void setupPrivateMessagingChannel() {
        RTopic privateMessagingChannel = getServerMessagingChannel(serviceName);
        privateMessagingChannel.addListener(Message.class, messageListener);
    }

    private RTopic getServerMessagingChannel(String serverName) {
        return redissonClient.getTopic("ServiceMessagingChannel_" + serverName.toLowerCase(), new SerializationCodec());
    }

    @Override
    public RedisMessageBuilder messageBuilder() {
        return new RedisMessageBuilder(getSessionUUID(), getSenderName());
    }

    @Override
    public void sendMessage(Message message) {
        globalMessagingChannel.publish(message);
    }

    @Override
    public void sendMessage(Message message, String... serverNames) {
        if (serverNames == null || serverNames.length == 0)
            return;
        for (String serverName : serverNames) {
            if (serverName.equals(serviceName))
                continue;
            getServerMessagingChannel(serverName).publish(message);
        }
    }

    @Override
    public boolean isOwnMessage(Message message) {
        return message.getSender().equals(getSessionUUID());
    }

    @Override
    public String getSenderName() {
        return serviceName + "_" + senderName;
    }

    @Override
    public EventBus getEventBus() {
        return eventBus;
    }

    @Override
    public boolean isLoaded() {
        return loaded;
    }

    @Override
    public void shutdown() {
        System.out.println("Shutting down Redis Messenger");
        globalMessagingChannel.removeListener(messageListener);
        System.out.println("Redis Messenger shut down successfully");
    }
}
