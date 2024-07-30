package ru.sparural.gradle.plugins.kafka.client.exceptions;

public class SourceLoadingException extends RuntimeException {
    public SourceLoadingException(Exception e) {
        super(e);
    }

    public SourceLoadingException(String msg) {
        super(msg);
    }
}
