package de.notion.messaging.instruction;

import com.google.common.eventbus.Subscribe;
import com.google.inject.Injector;
import de.notion.common.scheduler.Scheduler;
import de.notion.messaging.MessagingService;
import de.notion.messaging.event.MessageEvent;
import de.notion.messaging.instruction.annotation.InstructionInfo;
import de.notion.messaging.instruction.inject.InjectorProvider;
import de.notion.messaging.instruction.inject.InstructionRegistry;
import de.notion.messaging.message.Message;
import de.notion.messaging.message.MessageBuilder;
import de.notion.messaging.message.MessageWrapper;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

public class DefaultInstructionService implements InstructionService {

    private final MessagingService<? extends MessageBuilder> messagingService;
    private final Map<Integer, Class<? extends MessagingInstruction<?>>> instructionTypes;
    private final Map<UUID, MessagingInstruction<?>> pendingInstructions = new ConcurrentHashMap<>();
    private final Scheduler scheduler;
    private final InjectorProvider injectorProvider;

    protected DefaultInstructionService(MessagingService<? extends MessageBuilder> messagingService, InstructionRegistry registry) {
        this.messagingService = messagingService;
        this.instructionTypes = new ConcurrentHashMap<>(registry.getInstructionTypes());
        this.scheduler = new Scheduler();
        this.injectorProvider = new InjectorProvider();
        this.injectorProvider.create(registry);
        messagingService.getEventBus().register(this);

        scheduler.delay(() -> {
            for (UUID uuid : pendingInstructions.keySet()) {
                MessagingInstruction<?> instruction = pendingInstructions.get(uuid);
                if ((System.currentTimeMillis() - instruction.getCreationTimeStamp()) >= TimeUnit.SECONDS.toMillis(60))
                    pendingInstructions.remove(uuid);
            }
        }, 10, TimeUnit.SECONDS);
    }

    private UUID getSessionUUID() {
        return messagingService.getSessionUUID();
    }

    @Override
    public void sendInstruction(@NotNull MessagingInstruction<?> messagingInstruction) {
        InstructionInfo instructionInfo = getInstructionInfo(messagingInstruction.getClass());
        UUID uuid = messagingInstruction.getUuid();

        Message instructionMessage = constructMessage(messagingInstruction);
        if (instructionMessage == null)
            return;
        System.out.println("Sending Instruction [" + messagingInstruction.getUuid() + "] | Parameters [" + Arrays.toString(messagingInstruction.getParameters()) + "] | InstructionData[" + Arrays.toString(instructionMessage.dataToSend()) + "]"); //DEBUG
        messagingService.sendMessage(instructionMessage);
        if (instructionInfo.awaitsResponse())
            pendingInstructions.put(uuid, messagingInstruction);
    }

    @Override
    public void sendInstruction(@NotNull MessagingInstruction<?> messagingInstruction, String... serverNames) {
        InstructionInfo instructionInfo = getInstructionInfo(messagingInstruction.getClass());
        UUID uuid = messagingInstruction.getUuid();

        Message instructionMessage = constructMessage(messagingInstruction);
        if (instructionMessage == null)
            return;
        System.out.println("Sending Instruction [" + messagingInstruction.getUuid() + "] | Parameters [" + Arrays.toString(messagingInstruction.getParameters()) + "] | InstructionData[" + Arrays.toString(messagingInstruction.getData()) + "]"); //DEBUG
        messagingService.sendMessage(instructionMessage, serverNames);
        if (instructionInfo.awaitsResponse())
            pendingInstructions.put(uuid, messagingInstruction);
    }

    @Override
    public <T extends MessagingInstruction<?>> InstructionBuilder<T> instructionBuilder(Class<T> tClass, UUID instructionUUID) {
        return new InstructionBuilder<T>(tClass, instructionUUID) {
            @Override
            public T build() {
                T instruction = instantiateInstruction(tClass, uuid);
                instruction.withData(dataToSend);
                return instruction;
            }
        };
    }

    private Message constructMessage(@NotNull MessagingInstruction messagingInstruction) {
        if (messagingInstruction.getData() == null)
            throw new IllegalStateException("You can't send empty instructions");
        int instructionID = getID(messagingInstruction.getClass());
        if (instructionID == -1)
            throw new IllegalStateException("Sending an Instruction that has not been registered: " + messagingInstruction.getClass().getSimpleName());
        Message instructionMessage = messagingService
                .messageBuilder()
                .withParameters("VCoreInstruction")
                .withData(getSessionUUID(), instructionID, messagingInstruction.getUuid(), messagingInstruction.getParameters(), messagingInstruction.getData())
                .build();
        if (!messagingInstruction.onSend(messagingInstruction.getFuture(), messagingInstruction.getData())) {
            System.out.println("Cancelled Instruction [" + messagingInstruction.getUuid() + "]"); //DEBUG
            return null;
        }
        return instructionMessage;
    }

    private void sendResponse(int instructionID, UUID instructionUUID, String[] arguments, Object[] instructionData, Object[] responseData) {
        Message response = messagingService.messageBuilder()
                .withParameters("VCoreInstructionResponse")
                .withData(getSessionUUID(), instructionID, instructionUUID, arguments, instructionData, responseData)
                .build();
        System.out.println("Sending Instruction Response [" + instructionUUID + "] | Parameters [" + Arrays.toString(response.getParameters()) + "] | InstructionData[" + Arrays.toString(response.dataToSend()) + "]"); //DEBUG
        messagingService.sendMessage(response);
    }

    private int getID(Class<? extends MessagingInstruction> type) {
        for (Integer integer : instructionTypes.keySet()) {
            Class<? extends MessagingInstruction<?>> foundType = instructionTypes.get(integer);
            if (type.equals(foundType))
                return integer;
        }
        return -1;
    }

    private <T extends MessagingInstruction<?>> T instantiateInstruction(Class<? extends T> type, UUID instructionUUID) {
        Injector injector = injectorProvider.getInjector(type, instructionUUID);
        return injector.getInstance(type);
    }

    //TODO: Satt superclass maybe iwann anders lösen
    private InstructionInfo getInstructionInfo(@NotNull Class<? extends MessagingInstruction> type) {
        boolean found = false;
        int tries = 0;
        Class<?> typeToSearch = type;
        InstructionInfo instructionInfo = null;
        while (!found) {
            if (tries == 10)
                break;
            instructionInfo = typeToSearch.getSuperclass().getAnnotation(InstructionInfo.class);
            if (instructionInfo != null)
                found = true;
            typeToSearch = typeToSearch.getSuperclass();
            tries++;
        }
        if (instructionInfo == null)
            throw new IllegalStateException("Class " + type.getName() + " is missing InstructionInfo Annotation");
        return instructionInfo;
    }

    @Subscribe
    public void onMessage(MessageEvent messageEvent) {
        MessageWrapper messageWrapper = new MessageWrapper(messageEvent.getMessage());

        if (messageWrapper.parameterContains("VCoreInstruction") || messageWrapper.parameterContains("VCoreInstructionResponse")) {
            UUID senderUUID = messageEvent.getMessage().getData(0, UUID.class);
            int instructionID = messageEvent.getMessage().getData(1, Integer.class);
            UUID instructionUUID = messageEvent.getMessage().getData(2, UUID.class);
            String[] arguments = messageEvent.getMessage().getData(3, String[].class);
            Object[] instructionData = messageEvent.getMessage().getData(4, Object[].class);

            // Do not answer your own Instructions
            if (messageWrapper.parameterContains("VCoreInstruction") && senderUUID.equals(getSessionUUID()))
                return;

            Class<? extends MessagingInstruction<?>> type = instructionTypes.get(instructionID);
            if (type == null)
                return;
            if (messageWrapper.parameterContains("VCoreInstruction")) {
                InstructionInfo instructionInfo = getInstructionInfo(type);
                MessagingInstruction<?> responseInstruction = instantiateInstruction(type, instructionUUID);
                if (!(responseInstruction instanceof InstructionResponder))
                    return;
                System.out.println("" + senderUUID + " -> Received Instruction on " + messageEvent.getChannelName() + " [" + instructionUUID + "] | Parameters [" + Arrays.toString(arguments) + "] | InstructionData[" + Arrays.toString(instructionData) + "]"); //DEBUG
                InstructionResponder instructionResponder = (InstructionResponder) responseInstruction;
                if (pendingInstructions.containsKey(senderUUID)) {
                    if (!instructionResponder.respondToItself())
                        return;
                }
                Object[] responseData = instructionResponder.respondToInstruction(instructionData);
                if (responseData == null || responseData.length == 0)
                    return;
                if (instructionInfo.awaitsResponse())
                    sendResponse(instructionID, instructionUUID, arguments, instructionData, responseData);
            } else if (messageWrapper.parameterContains("VCoreInstructionResponse")) {
                Object[] responseData = messageEvent.getMessage().getData(5, Object[].class);
                System.out.println("" + senderUUID + " -> Received Instruction Response on " + messageEvent.getChannelName() + " [" + instructionUUID + "] | Parameters [" + Arrays.toString(arguments) + "] | InstructionData[" + Arrays.toString(instructionData) + "]"); //DEBUG
                if (!pendingInstructions.containsKey(instructionUUID))
                    return;
                MessagingInstruction<?> messagingInstruction = pendingInstructions.get(instructionUUID);
                if (!(messagingInstruction instanceof ResponseProcessor<?>))
                    return;
                ResponseProcessor<?> responseProcessor = (ResponseProcessor<?>) messagingInstruction;
                responseProcessor.onResponse((CompletableFuture) responseProcessor.getFuture(), instructionData, responseData);
                pendingInstructions.remove(instructionUUID);
            }
        }
    }

    @Override
    public boolean isLoaded() {
        return true;
    }

    @Override
    public void shutdown() {
        scheduler.waitUntilShutdown();
    }

    public Map<Integer, Class<? extends MessagingInstruction<?>>> getInstructionTypes() {
        return instructionTypes;
    }
}
