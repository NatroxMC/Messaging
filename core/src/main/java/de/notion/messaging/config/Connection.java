package de.notion.messaging.config;

import de.notion.common.system.SystemLoadable;
import de.notion.messaging.MessagingService;
import de.notion.messaging.sender.MessageEndPoint;

public interface Connection extends SystemLoadable {

    void load();

    MessageEndPoint construct(MessagingService messagingService);

}
