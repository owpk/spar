package ru.sparural.gradle.plugins.kafka.client.model.core;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import ru.sparural.gradle.plugins.kafka.client.model.core.visitors.ClassNameCollector;
import ru.sparural.gradle.plugins.kafka.client.model.core.visitors.JavaClassVisitor;
import ru.sparural.gradle.plugins.kafka.client.model.core.visitors.VisitorElement;

import java.util.Objects;

@Getter
@Builder
@Setter
public class JavaClass extends JavaCodeElement implements VisitorElement, Comparable<JavaClass> {
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

    @Override
    public int compareTo(JavaClass o) {
        var compare = o.packageName + o.typeName;
        var thisObj = this.typeName + this.typeName;
        return compare.compareTo(thisObj);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        JavaClass javaClass = (JavaClass) o;
        return Objects.equals(packageName, javaClass.packageName) && Objects.equals(typeName, javaClass.typeName);
    }

    @Override
    public int hashCode() {
        return Objects.hash(packageName, typeName);
    }
}
