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
    boolean isFinal;

    public static JavaVariable buildJavaVariable(String variableName, JavaClass variableType) {
        return JavaVariable.builder()
                .variableName(variableName)
                .type(variableType)
                .build();
    }

    public static JavaVariable buildJavaVariable(String variableName, String variableTypeName, String variableTypePackage) {
        return buildJavaVariable(variableTypeName, JavaClass.builder()
                .typeName(variableTypeName)
                .packageName(variableTypePackage)
                .build());
    }

    public String toView() {
        return String.format("%s %s", type.getCanonicalName(), variableName);
    }

}
