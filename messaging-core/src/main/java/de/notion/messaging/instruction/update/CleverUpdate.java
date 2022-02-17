package de.notion.messaging.instruction.update;

import org.jetbrains.annotations.NotNull;

import java.util.UUID;
import java.util.concurrent.CompletableFuture;

/**
 * A CleverUpdate is an Update that first executes the executeUpdate function on its own.
 * If the Function returns UpdateComplection.TRUE it will cancel the instruction as it has been completed successfully on its on platform
 * Else it will send the instruction to the network but will not answer it itself.
 */
public abstract class CleverUpdate extends Update {
    public CleverUpdate(@NotNull UUID uuid) {
        super(uuid);
    }

    @Override
    protected final boolean onSend(Object[] instructionData, CompletableFuture<Boolean> future) {
        return !executeUpdate(instructionData).toValue();
    }

    @Override
    public final boolean respondToItself() {
        return false;
    }
}
