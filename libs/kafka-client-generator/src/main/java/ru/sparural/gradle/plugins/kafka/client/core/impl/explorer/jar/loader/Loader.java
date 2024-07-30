package ru.sparural.gradle.plugins.kafka.client.core.impl.explorer.jar.loader;

import java.net.URL;

public interface Loader {
    Class<?> load(String className) throws Throwable;

    URL[] getUrls();
}
