package ru.sparural.gradle.plugins.kafka.client.model.core;

import lombok.Getter;
import lombok.Setter;
import ru.sparural.gradle.plugins.kafka.client.builder.Printable;
import ru.sparural.gradle.plugins.kafka.client.builder.JavaElementVisitor;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public abstract class JavaCodeElement implements AnnotationAware,
        ModifierAware, Printable {

    protected String annotation;
    protected Modifiers modifier = Modifiers.EMPTY;

    protected String begin;
    protected List<Printable> body;
    protected String end;

    @Override
    public void accept(JavaElementVisitor visitor, Integer tabs) {
        visitor.visit(this, tabs);
    }

    public void setBody(Printable printable) {
        body = List.of(printable);
    }

    public void addBody(Printable printable) {
        if (body == null)
            body = new ArrayList<>();
        body.add(printable);
    }

    public void addBody(List<Printable> printables) {
        if (body == null)
            body = printables;
        else body.addAll(printables);
    }
}
