package de.notion.messaging;

import de.notion.messaging.config.Connection;
import de.notion.messaging.sender.MessageEndPoint;
import org.jetbrains.annotations.NotNull;

public class MessagingServiceManager implements MessagingService {

    private final Connection connection;
    private final boolean loaded;

    public MessagingServiceManager(@NotNull Connection connection) {
        connection.load();
        this.connection = connection;
        this.loaded = true;
    }

    @Override
    public boolean isLoaded() {
        return loaded;
    }

    @Override
    public void shutdown() {

    }

    @Override
    public MessageEndPoint createEndPoint(String senderName) {
        return connection.construct(this);
    }
}
