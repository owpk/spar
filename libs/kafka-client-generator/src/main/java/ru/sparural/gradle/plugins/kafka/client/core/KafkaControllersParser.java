package ru.sparural.gradle.plugins.kafka.client.core;

import ru.sparural.gradle.plugins.kafka.client.exceptions.SourceLoadingException;
import ru.sparural.gradle.plugins.kafka.client.model.KafkaClientMethod;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Parses the source file, looks for the matching by passed annotations, builds the java code model
 */
public abstract class KafkaControllersParser<T> {
    protected SourceExplorer<T> sourceExplorer;
    protected String kafkaControllerAnnotation;
    protected String kafkaMappingAnnotation;

    public KafkaControllersParser(SourceExplorer<T> sourceExplorer,
                                  String kafkaControllerAnnotation, String kafkaMappingAnnotation) {
        this.sourceExplorer = sourceExplorer;
        this.kafkaControllerAnnotation = kafkaControllerAnnotation;
        this.kafkaMappingAnnotation = kafkaMappingAnnotation;
        if (kafkaMappingAnnotation == null || kafkaControllerAnnotation == null)
            throw new IllegalArgumentException(
                    "No annotation names provided e.g kafka controller annotation name and kafka mapping annotation name");
    }

    public Map<String, List<KafkaClientMethod>> parseKafkaMethods() throws SourceLoadingException {
        var sourceCodeFiles = sourceExplorer.walkSource();
        Map<String, List<KafkaClientMethod>> result = new HashMap<>();
        sourceCodeFiles.forEach(file -> result.putAll(processSourceCode(file)));
        return result;
    }

    protected abstract Map<String, List<KafkaClientMethod>> processSourceCode(T kafkaControllersSource);
}
