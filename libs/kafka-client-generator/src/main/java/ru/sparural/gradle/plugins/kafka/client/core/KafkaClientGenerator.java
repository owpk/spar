package ru.sparural.gradle.plugins.kafka.client.core;

import ru.sparural.gradle.plugins.kafka.client.core.impl.generator.SourceCodeEntry;
import ru.sparural.gradle.plugins.kafka.client.model.KafkaClientMethod;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public abstract class KafkaClientGenerator {
    protected KafkaControllersParser<?> kafkaControllersParser;

    public KafkaClientGenerator(KafkaControllersParser<?> kafkaControllersParser) {
        this.kafkaControllersParser = kafkaControllersParser;
    }

    public List<SourceCodeEntry> getEntriesToWrite() {
        List<SourceCodeEntry> entriesToWrite = kafkaControllersParser.parseKafkaMethods()
                .entrySet()
                .stream()
                .map(e -> processKafkaMethods(e.getKey(), e.getValue()))
                .collect(Collectors.toList());

        entriesToWrite.addAll(addClassesToGenerate());
        return entriesToWrite;
    }

    public abstract SourceCodeEntry processKafkaMethods(String controllerClassName, List<KafkaClientMethod> kafkaMethods);

    // Can be overridden to define additional classes to write as result
    protected List<SourceCodeEntry> addClassesToGenerate() {
        return Collections.emptyList();
    }

}
