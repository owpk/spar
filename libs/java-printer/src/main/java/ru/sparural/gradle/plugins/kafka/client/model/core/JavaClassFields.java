package ru.sparural.gradle.plugins.kafka.client.model.core;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class JavaClassFields extends JavaCodeElement {
    List<JavaVariable> variableList = new ArrayList<>();

    @Override
    public String getBegin() {
        return variableList.stream().map(f -> f.modifier + " " + f.getType().getCanonicalName() + " " + f.getVariableName() + ";\n")
                .collect(Collectors.joining());
    }

    public void addClassField(JavaVariable variable) {
        variableList.add(variable);
    }
}
