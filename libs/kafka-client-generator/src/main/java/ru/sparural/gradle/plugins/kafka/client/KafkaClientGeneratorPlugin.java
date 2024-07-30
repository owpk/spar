package ru.sparural.gradle.plugins.kafka.client;

import org.gradle.api.Plugin;
import org.gradle.api.Project;
import ru.sparural.gradle.plugins.kafka.client.dsl.KafkaClientGeneratorExtension;

public class KafkaClientGeneratorPlugin implements Plugin<Project> {

    @Override
    public void apply(Project project) {
        var extension = project.getExtensions().create("kafka", KafkaClientGeneratorExtension.class);
        project.getTasks().register("generateKafkaClient", KafkaClientGeneratorTask.class, extension, project);
    }
}
