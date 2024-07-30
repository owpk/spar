package ru.sparural.gradle.plugins.kafka.client.model.core;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.FieldDefaults;
import ru.sparural.gradle.plugins.kafka.client.utils.JavaClassPrinter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PROTECTED)
@ToString
public class JavaMethod extends JavaCodeElement {
    JavaClass exceptionThrows;
    boolean isStatic;
    boolean isSynchronized;
    String methodName;
    JavaClass returnType;
    List<JavaVariable> args = new ArrayList<>();

    @Override
    public String getBegin() {
        return modifier + (isStatic ? "static " : "") +
                (isSynchronized ? "synchronized " : "") +
                returnType.getCanonicalName() + " " + methodName +
                "(" + JavaClassPrinter.mapArgs(args) + ")" +
                (exceptionThrows != null ? " throws " + exceptionThrows.getSimpleName() : "") + " {";
    }

    @Override
    public String getEnd() {
        return "}";
    }

    public JavaMethod addMethodBody(String codeSnip) {
        var codeSnipp = new JavaCodeSnippet();
        codeSnipp.setCodeSnippet(codeSnip);
        this.addBody(codeSnipp);
        return this;
    }

    public JavaMethod addArg(JavaVariable arg) {
        args.add(arg);
        return this;
    }
}
