package de.notion.messaging.instruction;

import java.util.concurrent.CompletableFuture;

public interface InstructionSender<T> {
    boolean onSend(CompletableFuture<T> future, Object[] queryData);

    CompletableFuture<T> getFuture();
}
