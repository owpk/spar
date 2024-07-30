package ru.sparural.gradle.plugins.kafka.client.dsl;

import org.gradle.api.Action;
import org.gradle.api.provider.Property;
import org.gradle.api.tasks.Nested;

abstract public class KafkaClientGeneratorExtension {
    abstract public Property<String> getServiceName();

    abstract public Property<String> getKafkaMappingAnnotationName();

    abstract public Property<String> getKafkaControllerAnnotationName();

    @Nested
    abstract public GenerationSource getSource();

    public void source(Action<? super GenerationSource> action) {
        action.execute(getSource());
    }
}
