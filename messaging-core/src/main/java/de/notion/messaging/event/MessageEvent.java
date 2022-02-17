package de.notion.messaging.event;

import de.notion.messaging.message.Message;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public class MessageEvent {
    private final String channelName;
    private final Message message;

    public MessageEvent(@NotNull String channelName, @NotNull Message message) {
        Objects.requireNonNull(channelName, "channelName can't be null!");
        Objects.requireNonNull(message, "message can't be null!");
        this.channelName = channelName;
        this.message = message;
    }

    public Message getMessage() {
        return message;
    }

    public String getChannelName() {
        return channelName;
    }
}
