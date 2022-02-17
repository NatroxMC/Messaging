package de.notion.messaging.instruction;

import java.util.concurrent.CompletableFuture;

public interface ResponseProcessor<T> {
    void onResponse(CompletableFuture<T> future, Object[] queryData, Object[] responseData);

    CompletableFuture<T> getFuture();
}
