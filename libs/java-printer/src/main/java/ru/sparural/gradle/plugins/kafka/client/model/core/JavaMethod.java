package ru.sparural.gradle.plugins.kafka.client.model.core;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import ru.sparural.gradle.plugins.kafka.client.builder.JavaClassPrinter;

import java.util.List;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PROTECTED)
@Builder
public class JavaMethod extends JavaCodeElement {
    JavaClass exceptionThrows;
    boolean isStatic;
    String methodName;
    JavaClass returnType;
    List<JavaVariable> args;

    @Override
    public String getBegin() {
        return modifier + (isStatic ? "static " : "") +
                returnType.getCanonicalName() + " " + methodName +
                "(" + JavaClassPrinter.mapArgs(args) + ")" +
                (exceptionThrows != null ? " throws " + exceptionThrows.getSimpleName() : "") +  " {";
    }

    @Override
    public String getEnd() {
        return "}";
    }
}
