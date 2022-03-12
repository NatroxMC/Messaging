package de.notion.messaging.message;

import de.notion.messaging.message.def.DefaultMessage;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;
import java.util.UUID;

public class MessageBuilder {

    protected final UUID sender;
    protected final String senderIdentifier;
    protected Object[] dataToSend;

    public MessageBuilder(@NotNull UUID sender, @NotNull String senderIdentifier) {
        Objects.requireNonNull(sender, "sender can't be null!");
        Objects.requireNonNull(senderIdentifier, "senderIdentifier can't be null!");
        this.sender = sender;
        this.senderIdentifier = senderIdentifier;
    }

    public MessageBuilder withData(Object... dataToSend) {
        this.dataToSend = dataToSend;
        return this;
    }

    public Message build() {
        return new DefaultMessage(sender, senderIdentifier, dataToSend);
    }
}
