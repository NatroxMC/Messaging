package de.notion.messaging.instruction.inject;

import com.google.inject.AbstractModule;
import de.notion.messaging.instruction.MessagingInstruction;
import org.checkerframework.checker.index.qual.NonNegative;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class InstructionRegistry {

    private final Map<Integer, Class<? extends MessagingInstruction<?>>> instructionTypes;
    private final Map<Class<? extends MessagingInstruction<?>>, Set<AbstractModule>> registry;

    public InstructionRegistry() {
        this.instructionTypes = new HashMap<>();
        this.registry = new HashMap<>();
    }

    public void register(@NonNegative int id, Class<? extends MessagingInstruction<?>> dataClass, AbstractModule... modules) {
        if (instructionTypes.containsKey(id))
            throw new IllegalStateException("Id already registered: " + id);
        instructionTypes.put(id, dataClass);
        registry.putIfAbsent(dataClass, new HashSet<>());

        Set<AbstractModule> objectSet = registry.get(dataClass);
        objectSet.addAll(Arrays.asList(modules));
    }

    public Map<Class<? extends MessagingInstruction<?>>, Set<AbstractModule>> getRegistry() {
        return registry;
    }

    public Map<Integer, Class<? extends MessagingInstruction<?>>> getInstructionTypes() {
        return instructionTypes;
    }

}
