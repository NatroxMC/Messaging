package de.notion.messaging.example;

import com.google.inject.Inject;
import de.notion.messaging.instruction.update.Update;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.UUID;

public class TestUpdate extends Update {

    @Inject
    public TestUpdate(@NotNull UUID uuid) {
        super(uuid);
    }

    @Override
    public boolean respondToItself() {
        return false;
    }

    @Override
    protected List<Class<?>> dataTypes() {
        return null;
    }

    @Override
    protected List<String> parameters() {
        return null;
    }

    @NotNull
    @Override
    protected UpdateCompletion executeUpdate(Object[] instructionData) {


        return UpdateCompletion.TRUE;
    }
}
