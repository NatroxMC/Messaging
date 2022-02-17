package de.notion.messaging.instruction.annotation;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
public @interface InstructionInfo {
    boolean awaitsResponse();
}
