package de.notion.messaging.instruction;

import de.notion.common.system.SystemLoadable;
import de.notion.messaging.MessagingService;
import de.notion.messaging.instruction.inject.InstructionRegistry;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

public interface InstructionService extends SystemLoadable {

    static InstructionService create(MessagingService<?> messagingService, InstructionRegistry registry) {
        return new DefaultInstructionService(messagingService, registry);
    }

    void sendInstruction(@NotNull MessagingInstruction<?> messagingInstruction);

    void sendInstruction(@NotNull MessagingInstruction<?> messagingInstruction, String... serverNames);

    <T extends MessagingInstruction<?>> InstructionBuilder<T> instructionBuilder(Class<T> tClass, UUID uuid);

}
