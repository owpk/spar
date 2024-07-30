package ru.sparural.gradle.plugins.kafka.client.core.impl.explorer.jar.loader;

import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.Path;

public class CustomURLClassLoader implements Loader {
    private static final String DEFAULT_PROTOCOL = "file://";
    private final URLClassLoader urlClassLoader;
    private final String protocol;
    private final URL[] url;

    public CustomURLClassLoader(Path path) {
        this(path, DEFAULT_PROTOCOL);
    }

    public CustomURLClassLoader(Path path, String protocol) {
        this.protocol = protocol;
        this.url = createURLFromPath(path.toString());
        urlClassLoader = new URLClassLoader(url);
    }

    public Class<?> load(String className) throws Throwable {
        return urlClassLoader.loadClass(className);
    }

    @Override
    public URL[] getUrls() {
        return url;
    }

    private URL[] createURLFromPath(String path) {
        try {
            path = protocol + path;
            return new URL[]{new URL(path)};
        } catch (MalformedURLException e) {
            throw new RuntimeException(e.getLocalizedMessage());
        }
    }
}
