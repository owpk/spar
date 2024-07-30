package ru.sparural.gradle.plugins.kafka.client.core.impl.generator;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.experimental.FieldDefaults;

@AllArgsConstructor
@Getter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class SourceCodeEntry {
    String packageName;
    String className;
    String sourceCode;
}
