package ru.sparural.gradle.plugins.kafka.client.model.core;


import lombok.*;
import lombok.experimental.FieldDefaults;

@ToString
@FieldDefaults(level = AccessLevel.PROTECTED)
@Getter
@Setter
@Builder
public class JavaVariable extends JavaCodeElement {
    JavaClass type;
    String variableName;

    public String toView() {
        return String.format("%s %s", type.getCanonicalName(), variableName);
    }
}
