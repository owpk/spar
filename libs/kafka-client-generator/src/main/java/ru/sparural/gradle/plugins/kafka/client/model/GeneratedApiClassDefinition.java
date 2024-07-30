package ru.sparural.gradle.plugins.kafka.client.model;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import ru.sparural.gradle.plugins.kafka.client.model.core.JavaClass;
import ru.sparural.gradle.plugins.kafka.client.model.core.JavaClassDefinition;
import ru.sparural.gradle.plugins.kafka.client.model.core.JavaVariable;
import ru.sparural.gradle.plugins.kafka.client.model.core.Modifiers;
import ru.sparural.gradle.plugins.kafka.client.model.core.visitors.ImportCollector;

import java.util.List;
import java.util.stream.Collectors;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class GeneratedApiClassDefinition extends JavaClassDefinition {
    JavaClass completableFuture;
    JavaVariable kafkaRequestBuilderField;
    JavaVariable reqTopicName;
    List<KafkaClientMethod> kafkaClientMethods;

    public GeneratedApiClassDefinition(String packageName,
                                       String className,
                                       JavaVariable kafkaRequestBuilderField,
                                       JavaVariable kafkaRequestTopicName,
                                       List<KafkaClientMethod> kafkaClientMethods) {
        this.modifier = Modifiers.PUBLIC;
        this.className = className;
        this.packageName = packageName;
        completableFuture = JavaClass.builder()
                .packageName("java.util.concurrent")
                .typeName("CompletableFuture")
                .build();

        this.kafkaRequestBuilderField = kafkaRequestBuilderField;
        this.kafkaClientMethods = kafkaClientMethods;

        this.reqTopicName = kafkaRequestTopicName;

        var importCollector = new ImportCollector();
        importCollector.visit(kafkaRequestBuilderField.getType());
        importCollector.visit(completableFuture);

        kafkaClientMethods.forEach(m -> {
            importCollector.visit(m.getReturnType());
            importCollector.visit(m.getPayload() != null ? m.getPayload().getType() : null);
            m.getRequestParams().forEach(p -> importCollector.visit(p.getType()));
        });
        addImports(importCollector.getImports().stream().distinct().sorted().collect(Collectors.toList()));
    }

}
