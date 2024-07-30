package ru.sparural.gradle.plugins.kafka.client.model.core;


import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class JavaCodeSnippet extends JavaCodeElement {
    protected String codeSnippet;

    @Override
    public String getBegin() {
        return codeSnippet;
    }
}
