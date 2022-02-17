package de.notion.messaging.message;

import java.io.Serializable;
import java.util.UUID;

public interface Message extends Serializable {

    UUID getSender();

    String getSenderIdentifier();

    String[] getParameters();

    Object[] dataToSend();

    default int size() {
        if (dataToSend() == null)
            return 0;
        return dataToSend().length;
    }

    default boolean isTypeOf(int index, Class<?> type) {
        if (index < 0 || index >= size())
            return false;
        return dataToSend()[index].getClass().equals(type);
    }

    default boolean isAssignableFrom(int index, Class<?> type) {
        if (index < 0 || index >= size())
            return false;
        return dataToSend()[index].getClass().isAssignableFrom(type);
    }

    default <T> T getData(int index, Class<? extends T> type) {
        if (!isTypeOf(index, type))
            throw new ClassCastException("Cannot cast data in index[" + index + "] to " + type + "!");
        return type.cast(dataToSend()[index]);
    }

}
