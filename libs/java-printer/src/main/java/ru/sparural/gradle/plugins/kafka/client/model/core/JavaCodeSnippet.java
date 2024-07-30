package ru.sparural.gradle.plugins.kafka.client.model.core;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public class JavaCodeSnippet extends JavaCodeElement {
    private String codeSnippet;

    @Override
    public String getBegin() {
        return codeSnippet;
    }
}
