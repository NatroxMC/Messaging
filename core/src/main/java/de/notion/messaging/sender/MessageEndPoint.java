package de.notion.messaging.sender;

import de.notion.messaging.channel.MessageChannel;
import de.notion.messaging.message.Message;
import de.notion.messaging.message.MessageBuilder;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

public interface MessageEndPoint {

    @NotNull
    MessageBuilder messageBuilder();

    @NotNull
    MessageChannel channel(String name);

    @NotNull
    default MessageChannel globalChannel() {
        return channel("Global");
    }

    boolean isOwnMessage(Message message);

    @NotNull
    default UUID sessionUUID() {
        return UUID.randomUUID();
    }

}
