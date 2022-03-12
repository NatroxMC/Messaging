package de.notion.messaging.message;

import java.io.Serializable;
import java.util.UUID;

public interface Message extends Serializable {

    UUID sender();

    String senderIdentifier();

    Object[] data();

    default int size() {
        if (data() == null)
            return 0;
        return data().length;
    }

    default boolean isTypeOf(int index, Class<?> type) {
        if (index < 0 || index >= size())
            return false;
        return data()[index].getClass().equals(type);
    }

    default boolean isAssignableFrom(int index, Class<?> type) {
        if (index < 0 || index >= size())
            return false;
        return data()[index].getClass().isAssignableFrom(type);
    }

    default <T> T data(int index, Class<? extends T> type) {
        if (!isTypeOf(index, type))
            throw new ClassCastException("Cannot cast data in index[" + index + "] to " + type + "!");
        return type.cast(data()[index]);
    }

    default boolean validate(Class<?>... types) {
        if (size() != types.length)
            return false;

        for (int i = 0; i < types.length; i++) {
            Class<?> type = types[i];
            if (!isTypeOf(i, type))
                return false;
        }
        return true;
    }

}
