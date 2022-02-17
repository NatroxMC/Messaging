package de.notion.messaging.instruction;

import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

public abstract class MessagingInstruction<T> implements InstructionSender<T> {

    protected final String[] parameters;
    protected final List<Class<?>> types;
    protected final UUID uuid;
    private final Long creationTimeStamp = System.currentTimeMillis();
    private Object[] data;

    public MessagingInstruction(@NotNull UUID uuid) {
        Objects.requireNonNull(uuid, "uuid can't be null!");
        this.uuid = uuid;
        this.parameters = parameters().toArray(new String[]{});
        this.types = dataTypes();
    }

    public MessagingInstruction<T> withData(Object... data) {
        if (data.length != types.size())
            throw new IllegalStateException("Wrong Input Parameter Length for " + getClass().getSimpleName() + " [" + dataTypes().size() + "]");
        for (int i = 0; i < types.size(); i++) {
            Class<?> type = types.get(i);
            Object datum = data[i];
            if (!type.isAssignableFrom(datum.getClass()))
                throw new IllegalStateException(datum + " is not type or subtype of " + type.getName());

        }
        this.data = data;
        return this;
    }

    public UUID getUuid() {
        return uuid;
    }

    public String[] getParameters() {
        return parameters;
    }

    public Object[] getData() {
        return data;
    }

    public Long getCreationTimeStamp() {
        return creationTimeStamp;
    }

    protected abstract List<Class<?>> dataTypes();

    protected abstract List<String> parameters();

    @Override
    public String toString() {
        return "MessagingInstruction{" +
                "parameters=" + Arrays.toString(parameters) +
                ", types=" + types +
                ", uuid=" + uuid +
                ", creationTimeStamp=" + creationTimeStamp +
                ", data=" + Arrays.toString(data) +
                '}';
    }
}
