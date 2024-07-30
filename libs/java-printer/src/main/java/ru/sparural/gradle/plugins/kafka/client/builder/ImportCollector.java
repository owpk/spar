package ru.sparural.gradle.plugins.kafka.client.builder;

import ru.sparural.gradle.plugins.kafka.client.model.core.JavaClass;

import java.util.LinkedHashSet;
import java.util.Set;
import java.util.stream.Collectors;

public class ImportCollector implements JavaClassVisitor {
    private final Set<String> imports;

    public ImportCollector() {
        this.imports = new LinkedHashSet<>();
    }

    public Set<String> getImports() {
        return imports.stream()
                .sorted()
                .collect(Collectors.toCollection(LinkedHashSet::new));
    }

    @Override
    public void visit(JavaClass javaClass) {
        if (javaClass != null && javaClass.getPackageName()
                != null && javaClass.getFullName()
                != null && !javaClass.getFullName().isBlank()) {
            imports.add(String.format("%s", javaClass.getFullName()));
            if (javaClass.getDiamondType() != null)
                javaClass.getDiamondType().accept(this);
        }
    }
}
