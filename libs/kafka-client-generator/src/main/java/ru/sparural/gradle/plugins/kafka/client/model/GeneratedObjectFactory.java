package ru.sparural.gradle.plugins.kafka.client.model;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.FieldDefaults;
import ru.sparural.gradle.plugins.kafka.client.model.core.JavaClassDefinition;
import ru.sparural.gradle.plugins.kafka.client.model.core.JavaVariable;
import ru.sparural.gradle.plugins.kafka.client.model.core.Modifiers;
import ru.sparural.gradle.plugins.kafka.client.utils.JavaClassPrinter;

import java.util.Locale;

@FieldDefaults(level = AccessLevel.PRIVATE)
@Getter
public class GeneratedObjectFactory extends JavaClassDefinition {
    final JavaVariable requestBuilder;
    final JavaVariable topicName;

    public GeneratedObjectFactory(String basePackage,
                                  String serviceName,
                                  JavaVariable requestBuilder,
                                  JavaVariable topicName) {
        this.packageName = basePackage;
        this.requestBuilder = requestBuilder;
        this.topicName = topicName;

        var capitalized = JavaClassPrinter.classNameFromVariable(
                serviceName.toLowerCase(Locale.ROOT));
        this.className = capitalized + "ClientFactory";
        this.modifier = Modifiers.PUBLIC;
    }
}
