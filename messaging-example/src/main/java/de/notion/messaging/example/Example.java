package de.notion.messaging.example;

import com.google.common.eventbus.Subscribe;
import com.google.inject.AbstractModule;
import de.notion.messaging.MessagingService;
import de.notion.messaging.config.MessagingConfig;
import de.notion.messaging.event.MessageEvent;
import de.notion.messaging.instruction.InstructionService;
import de.notion.messaging.instruction.inject.InstructionRegistry;
import de.notion.messaging.redis.config.RedisMessagingConfig;

public class Example {

    public static void main(String[] args) {
        MessagingConfig config = new RedisMessagingConfig(false, "", "redis://127.0.0.1:6379");
        MessagingService<?> messagingService = MessagingService.create(config, "", "");

        InstructionRegistry registry = new InstructionRegistry();
        registry.register(0, TestUpdate.class);

        InstructionService instructionService = InstructionService.create(messagingService, registry);
    }

    @Subscribe
    public void handleMessage(MessageEvent event) {

    }

}