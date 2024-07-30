package ru.sparural.gradle.plugins.kafka.client.core.impl.explorer.jar;

import org.gradle.api.logging.Logger;
import org.gradle.api.logging.Logging;
import ru.sparural.gradle.plugins.kafka.client.core.SourceCodeProvider;
import ru.sparural.gradle.plugins.kafka.client.core.SourceExplorer;
import ru.sparural.gradle.plugins.kafka.client.core.impl.explorer.jar.loader.CustomURLClassLoader;
import ru.sparural.gradle.plugins.kafka.client.core.impl.explorer.jar.loader.Loader;
import ru.sparural.gradle.plugins.kafka.client.core.impl.provider.JarSourceCodeProviderWrapper;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.LinkedList;
import java.util.List;

public class JarSourceExplorerImpl extends SourceExplorer<Class<?>> {
    private static final Logger LOGGER = Logging.getLogger(JarSourceExplorerImpl.class);

    public JarSourceExplorerImpl(SourceCodeProvider provider) {
        super(provider);
    }

    @Override
    protected List<Class<?>> walkSource(Path jarFileSystem) {
        var jarProvider = (JarSourceCodeProviderWrapper) this.provider;
        var pathToJar = jarProvider.getPathToJar();
        return scanJar(new CustomURLClassLoader(pathToJar), jarFileSystem, new BaseJarVisitor());
    }

    public List<Class<?>> scanJar(Loader loader, Path path, Visitor visitor) {
        LOGGER.error("Scanning jar file system: " + path.toUri());
        try {
            Files.walkFileTree(path, visitor);
            var result = visitor.getClassNames();
            var map = new LinkedList<Class<?>>();

            for (String className : result) {
                try {
                    var loadedClass = loader.load(className);
                    LOGGER.debug("Success class loading: " + loadedClass);
                    map.add(loadedClass);
                } catch (Throwable e) {
                    LOGGER.info("Cannot load class: " + e.getLocalizedMessage());
                }
            }
            return map;
        } catch (IOException e) {
            throw new RuntimeException(e.getLocalizedMessage());
        }
    }
}
