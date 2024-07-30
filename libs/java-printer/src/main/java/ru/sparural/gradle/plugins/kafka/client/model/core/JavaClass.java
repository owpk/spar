package ru.sparural.gradle.plugins.kafka.client.model.core;

import lombok.Builder;
import lombok.Getter;
import ru.sparural.gradle.plugins.kafka.client.builder.ClassNameCollector;
import ru.sparural.gradle.plugins.kafka.client.builder.JavaClassVisitor;
import ru.sparural.gradle.plugins.kafka.client.builder.VisitorElement;

@Getter
@Builder
public class JavaClass extends JavaCodeElement implements VisitorElement {
    String packageName;
    String typeName;
    JavaClass diamondType;

    public String getFullName() {
        return String.format("%s.%s", packageName, typeName);
    }

    public String getSimpleName() {
        return typeName;
    }

    public String getCanonicalName() {
        var nameCollector = new ClassNameCollector();
        nameCollector.visit(this);
        return nameCollector.getResult();
    }

    @Override
    public String toString() {
        return "{ package: " + packageName + ", class: " + typeName + ", diamond: " +
                (diamondType != null ? diamondType.getSimpleName() : "null") + " }";
    }

    @Override
    public void accept(JavaClassVisitor visitor) {
        visitor.visit(this);
    }

}
