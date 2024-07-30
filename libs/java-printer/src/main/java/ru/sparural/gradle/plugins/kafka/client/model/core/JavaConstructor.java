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
@Builder
@FieldDefaults(level = AccessLevel.PROTECTED)
public class JavaConstructor extends JavaCodeElement {
    String constructorName;
    List<JavaVariable> args;

    @Override
    public String getBegin() {
        return modifier + constructorName +
                "(" + JavaClassPrinter.mapArgs(args) + ") {";
    }

    @Override
    public String getEnd() {
        return "}";
    }
}
