package de.notion.messaging;

import de.notion.common.system.SystemLoadable;
import de.notion.messaging.config.Connection;
import de.notion.messaging.sender.MessageEndPoint;
import org.jetbrains.annotations.NotNull;

public interface MessagingService extends SystemLoadable {

    @NotNull
    static MessagingService create(@NotNull Connection config) {
        return new MessagingServiceManager(config);
    }

    MessageEndPoint createEndPoint(String senderName);

}
