package de.notion.messaging.channel;

import com.google.common.eventbus.EventBus;
import de.notion.messaging.message.Message;

public interface MessageChannel {

    void sendMessage(Message message);

    EventBus eventBus();

}
