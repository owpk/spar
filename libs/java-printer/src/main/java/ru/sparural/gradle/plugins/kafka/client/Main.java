package ru.sparural.gradle.plugins.kafka.client;

import org.jboss.forge.roaster.Roaster;
import org.jboss.forge.roaster.model.JavaUnit;
import ru.sparural.gradle.plugins.kafka.client.model.core.SourceCode;

public class Main {
    public static void main(String[] args) {
        String javaCode = SourceCode.code;

        JavaUnit unit = Roaster.parseUnit(javaCode);
    }
}