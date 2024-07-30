package ru.sparural.gradle.plugins.kafka.client.model.core;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import ru.sparural.gradle.plugins.kafka.client.builder.JavaClassPrinter;

import java.util.List;
import java.util.stream.Collectors;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PROTECTED)
public class JavaClassDefinition extends JavaCodeElement {
    String packageName;
    List<JavaClass> imports;
    String className;

    @Override
    public String getBegin() {
        var pck = "package " + packageName + ";\n\n";
        var imps = imports
                .stream().map(imp -> JavaClassPrinter.addImport(imp.getPackageName() + "." + imp.getSimpleName()) + "\n")
                .collect(Collectors.joining());
        var classDef = modifier + "class " + className + " {\n";
        return pck + imps + classDef;
    }

    @Override
    public String getEnd() {
        return "}";
    }
}
