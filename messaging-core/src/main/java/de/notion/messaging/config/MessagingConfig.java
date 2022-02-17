package de.notion.messaging.config;

import de.notion.common.system.SystemLoadable;
import de.notion.messaging.MessagingService;

public interface MessagingConfig extends SystemLoadable {

    void load();

    MessagingService<?> construct(String serviceName, String senderName);
}
