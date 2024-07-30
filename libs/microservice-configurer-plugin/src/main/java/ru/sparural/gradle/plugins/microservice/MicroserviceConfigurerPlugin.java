package ru.sparural.gradle.plugins.microservice;

import org.gradle.api.Plugin;
import org.gradle.api.Project;

public class MicroserviceConfigurerPlugin implements Plugin<Project> {

    @Override
    public void apply(Project project) {
        project.getExtensions().create("microservice", MicroserviceConfigurerExtension.class, project);
    }
}
