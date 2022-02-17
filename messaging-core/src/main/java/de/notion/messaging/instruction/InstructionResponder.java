package de.notion.messaging.instruction;

public interface InstructionResponder {
    Object[] respondToInstruction(Object[] instructionData);

    boolean respondToItself();
}
