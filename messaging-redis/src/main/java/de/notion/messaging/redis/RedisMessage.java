package de.notion.messaging.redis;

import de.notion.messaging.message.Message;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.UUID;

public class RedisMessage implements Message {

    private final UUID sender;
    private final String senderIdentifier;
    private final String[] parameters;
    private final Object[] dataToSend;

    RedisMessage(@NotNull UUID sender, @NotNull String senderIdentifier, @NotNull String[] parameters, @NotNull Object[] dataToSend) {
        this.sender = sender;
        this.senderIdentifier = senderIdentifier;
        this.parameters = parameters;
        this.dataToSend = dataToSend;
    }

    @Override
    public UUID getSender() {
        return sender;
    }

    @Override
    public String getSenderIdentifier() {
        return senderIdentifier;
    }

    @Override
    public String[] getParameters() {
        return parameters;
    }

    @Override
    public Object[] dataToSend() {
        return dataToSend;
    }

    @Override
    public String toString() {
        return "SimpleRedisMessage{" +
                "sender=" + sender +
                ", senderIdentifier='" + senderIdentifier + '\'' +
                ", parameters=" + Arrays.toString(parameters) +
                ", dataToSend=" + Arrays.toString(dataToSend) +
                '}';
    }
}
