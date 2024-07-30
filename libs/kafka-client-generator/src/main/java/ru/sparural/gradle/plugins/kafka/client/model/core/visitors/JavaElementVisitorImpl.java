package ru.sparural.gradle.plugins.kafka.client.model.core.visitors;

import ru.sparural.gradle.plugins.kafka.client.model.core.JavaCodeElement;

import java.util.Arrays;
import java.util.stream.Collectors;

public class JavaElementVisitorImpl implements JavaElementVisitor {

    private final StringBuilder result = new StringBuilder();

    @Override
    public void visit(JavaCodeElement element, Integer tabs) {
        var beg = element.getBegin();
        var curTabs = "\t".repeat(tabs);
        var parts = beg.split("\n");
        var begin = Arrays.stream(parts).map(x -> curTabs + x + "\n")
                .collect(Collectors.joining());
        result.append(begin);
        if (element.getBody() != null)
            element.getBody().forEach(
                    b -> b.accept(this, tabs + 1));
        var end = element.getEnd();
        if (end != null)
            result.append(curTabs)
                    .append(end).append("\n\n");
    }

    public String getResult() {
        return result.toString();
    }
}
