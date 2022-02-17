package de.notion.messaging.instruction.query;

import de.notion.messaging.instruction.InstructionResponder;
import de.notion.messaging.instruction.MessagingInstruction;
import de.notion.messaging.instruction.ResponseProcessor;
import de.notion.messaging.instruction.annotation.InstructionInfo;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;
import java.util.concurrent.CompletableFuture;

@InstructionInfo(awaitsResponse = true)
public abstract class Query<T> extends MessagingInstruction<T> implements InstructionResponder, ResponseProcessor<T> {

    private final CompletableFuture<T> future = new CompletableFuture<>();

    public Query(@NotNull UUID uuid) {
        super(uuid);

    }

    @Override
    public CompletableFuture<T> getFuture() {
        return future;
    }

    @Override
    public boolean onSend(CompletableFuture<T> future, Object[] queryData) {
        return true;
    }
}
