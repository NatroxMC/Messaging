package de.notion.messaging.instruction.update;

import de.notion.messaging.instruction.InstructionResponder;
import de.notion.messaging.instruction.annotation.InstructionInfo;
import de.notion.messaging.instruction.query.Query;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;
import java.util.concurrent.CompletableFuture;

@InstructionInfo(awaitsResponse = true)
public abstract class Update extends Query<Boolean> implements InstructionResponder {

    public Update(@NotNull UUID uuid) {
        super(uuid);
    }

    @NotNull
    protected abstract UpdateCompletion executeUpdate(Object[] instructionData);

    protected boolean onSend(Object[] instructionData, CompletableFuture<Boolean> future) {
        return true;
    }

    @Override
    public final void onResponse(CompletableFuture<Boolean> future, Object[] queryData, Object[] responseData) {
        future.complete((Boolean) responseData[0]);
    }

    @Override
    public final Object[] respondToInstruction(Object[] instructionData) {
        UpdateCompletion updateCompletion = executeUpdate(instructionData);
        switch (updateCompletion) {
            case TRUE:
                return new Object[]{true};
            case FALSE:
                return new Object[]{false};
            default:
                return null;
        }
    }

    @Override
    public final boolean onSend(CompletableFuture<Boolean> future, Object[] queryData) {
        return onSend(queryData, future);
    }

    public enum UpdateCompletion {
        TRUE(true),
        FALSE(false),
        NOTHING(false),
        ;
        private final boolean value;

        UpdateCompletion(boolean value) {
            this.value = value;
        }

        public boolean toValue() {
            return value;
        }
    }
}
