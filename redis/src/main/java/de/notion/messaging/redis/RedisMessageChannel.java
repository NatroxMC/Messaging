package de.notion.messaging.redis;

import de.notion.messaging.channel.AbstractMessageChannel;
import de.notion.messaging.event.MessageEvent;
import de.notion.messaging.message.Message;
import org.redisson.api.RTopic;
import org.redisson.api.RedissonClient;
import org.redisson.api.listener.MessageListener;
import org.redisson.codec.SerializationCodec;

public class RedisMessageChannel extends AbstractMessageChannel {

    private final RedissonClient redissonClient;
    private final RTopic topic;
    private final MessageListener<Message> messageListener;

    public RedisMessageChannel(String channelName, RedisMessageEndPoint messageEndPoint) {
        super(channelName);
        this.redissonClient = messageEndPoint.redissonClient();
        this.topic = redissonClient.getTopic(channelName, new SerializationCodec());
        this.messageListener = (channel, msg) -> {
            // Own Messages won't throw an event
            if (messageEndPoint.isOwnMessage(msg))
                return;
            eventBus.post(new MessageEvent(channel.toString(), msg));
        };
        this.topic.addListener(Message.class, messageListener);
    }

    @Override
    public void sendMessage(Message message) {
        topic.publish(message);
    }
}
