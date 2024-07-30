package ru.sparural.gradle.plugins.kafka.client.model.core;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import ru.sparural.gradle.plugins.kafka.client.utils.JavaClassPrinter;

import java.util.List;
import java.util.stream.Collectors;

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

    public void assignArgsToFields() {
        var bindings = new JavaCodeSnippet();
        var snippet = this.args.stream()
                .map(x -> JavaClassPrinter.assignVariable(x.getVariableName(), x.getVariableName()))
                .collect(Collectors.joining());
        bindings.setCodeSnippet(snippet);
        this.addBody(bindings);
    }

    @Override
    public String getEnd() {
        return "}";
    }
}
