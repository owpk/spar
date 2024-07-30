package ru.sparural.gradle.plugins.kafka.client.model.core.visitors;

import ru.sparural.gradle.plugins.kafka.client.model.core.JavaClass;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class ImportCollector implements JavaClassVisitor {
    private final Set<JavaClass> imports;

    public ImportCollector() {
        this.imports = new LinkedHashSet<>();
    }

    public List<JavaClass> getImports() {
        return imports.stream()
                .sorted().distinct()
                .filter(jc -> jc.getPackageName() != null &&
                        !jc.getPackageName().equals("java.lang"))
                .collect(Collectors.toList());
    }

    @Override
    public void visit(JavaClass javaClass) {
        if (javaClass != null && javaClass.getPackageName()
                != null && javaClass.getFullName()
                != null && !javaClass.getFullName().isBlank()) {
            imports.add(javaClass);
            if (javaClass.getDiamondType() != null)
                javaClass.getDiamondType().accept(this);
        }
    }
}
