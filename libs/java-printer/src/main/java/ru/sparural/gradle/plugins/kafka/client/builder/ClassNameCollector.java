package ru.sparural.gradle.plugins.kafka.client.builder;

import ru.sparural.gradle.plugins.kafka.client.model.core.JavaClass;


public class ClassNameCollector implements JavaClassVisitor {
    private final StringBuilder sb = new StringBuilder();

    @Override
    public void visit(JavaClass javaClass) {
        sb.append(javaClass.getSimpleName());
        if (javaClass.getDiamondType() != null) {
            sb.append("<");
            javaClass.getDiamondType().accept(this);
        }
    }

    public String getResult() {
        var str = sb.toString();
        var countStartBr = str.chars().filter(ch -> ch == '<').count();
        var closeBr = ">".repeat((int) countStartBr);
        return str + closeBr;
    }

}
