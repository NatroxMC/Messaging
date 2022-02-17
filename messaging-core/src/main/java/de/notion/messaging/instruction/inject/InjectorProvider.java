package de.notion.messaging.instruction.inject;

import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Injector;
import de.notion.messaging.instruction.MessagingInstruction;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

public class InjectorProvider {

    private final Map<Class<? extends MessagingInstruction<?>>, Injector> injectors;

    public InjectorProvider() {
        this.injectors = new HashMap<>();
    }

    public void create(InstructionRegistry registry) {
        for (Map.Entry<Class<? extends MessagingInstruction<?>>, Set<AbstractModule>> entry : registry.getRegistry().entrySet()) {
            injectors.put(entry.getKey(), createInjector(entry.getValue()));
        }
    }

    private Injector createInjector(Set<AbstractModule> modules) {
        return Guice.createInjector(modules);
    }

    @Nullable
    public Injector getInjector(Class<? extends MessagingInstruction<?>> dataClass) {
        return injectors.get(dataClass);
    }

    public Injector getInjector(Class<? extends MessagingInstruction<?>> dataClass, UUID uuid) {
        Injector injector = getInjector(dataClass);
        AbstractModule module = new AbstractModule() {
            @Override
            protected void configure() {
                bind(UUID.class).toInstance(uuid);
            }
        };

        if (injector != null) {
            return injector.createChildInjector(module);
        }

        return Guice.createInjector(module);
    }

}
