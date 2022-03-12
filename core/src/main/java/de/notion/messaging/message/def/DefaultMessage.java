package de.notion.messaging.message.def;

import de.notion.messaging.message.Message;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

public class DefaultMessage implements Message {

    private final UUID sender;
    private final String senderIdentifier;
    private final Object[] dataToSend;

    public DefaultMessage(@NotNull UUID sender, @NotNull String senderIdentifier, @NotNull Object[] dataToSend) {
        this.sender = sender;
        this.senderIdentifier = senderIdentifier;
        this.dataToSend = dataToSend;
    }

    @Override
    public UUID sender() {
        return sender;
    }

    @Override
    public String senderIdentifier() {
        return senderIdentifier;
    }

    @Override
    public Object[] data() {
        return dataToSend;
    }
}
