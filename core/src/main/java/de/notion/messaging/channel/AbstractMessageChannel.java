package de.notion.messaging.channel;

import com.google.common.eventbus.EventBus;

public abstract class AbstractMessageChannel implements MessageChannel {

    protected final String channelName;
    protected final EventBus eventBus;

    public AbstractMessageChannel(String channelName) {
        this.channelName = channelName;
        this.eventBus = new EventBus();
    }

    @Override
    public EventBus eventBus() {
        return eventBus;
    }
}
