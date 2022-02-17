package de.notion.messaging;

import com.google.common.eventbus.EventBus;
import de.notion.common.system.SystemLoadable;
import de.notion.messaging.config.MessagingConfig;
import de.notion.messaging.message.Message;
import de.notion.messaging.message.MessageBuilder;

import java.util.UUID;

public interface MessagingService<T extends MessageBuilder> extends SystemLoadable {

    static MessagingService<?> create(MessagingConfig config, String serviceName, String senderName) {
        System.out.println("Constructing MessagingService");
        System.out.println("Searching for implementation...");
        if (config != null) {
            config.load();
            return config.construct(serviceName, senderName);
        } else {
            throw new IllegalStateException("MessagingType not implemented yet");
        }
    }

    T messageBuilder();

    void sendMessage(Message message);

    void sendMessage(Message message, String... serviceName);

    void setupPrivateMessagingChannel();

    boolean isOwnMessage(Message message);

    default UUID getSessionUUID() {
        return UUID.randomUUID();
    }

    String getSenderName();

    EventBus getEventBus();
}
