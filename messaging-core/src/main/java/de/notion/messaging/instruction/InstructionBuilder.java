package de.notion.messaging.instruction;

import java.util.UUID;

public abstract class InstructionBuilder<T extends MessagingInstruction<?>> {

    protected final Class<T> tClass;
    protected final UUID uuid;
    protected Object[] dataToSend;

    public InstructionBuilder(Class<T> tClass, UUID uuid) {
        this.tClass = tClass;
        this.uuid = uuid;
    }

    public InstructionBuilder<T> withData(Object... data) {
        this.dataToSend = data;
        return this;
    }

    public abstract T build();

}
