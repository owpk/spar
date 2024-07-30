package ru.sparural.gradle.plugins.kafka.client.core.impl.provider;

import ru.sparural.gradle.plugins.kafka.client.core.GradleBuildable;
import ru.sparural.gradle.plugins.kafka.client.core.SourceCodeProvider;
import ru.sparural.gradle.plugins.kafka.client.exceptions.SourceLoadingException;
import ru.sparural.gradle.plugins.kafka.client.utils.GradleBuildManager;

import java.io.IOException;
import java.net.URI;
import java.nio.file.*;
import java.util.HashMap;

public final class JarSourceCodeProviderWrapper implements SourceCodeProvider {
    private final Path buildRoot;

    private final Path pathToJar;

    private FileSystem jarFileSystem;

    public JarSourceCodeProviderWrapper(SourceCodeProvider sourceCodeProvider,
                                        String gradleModule, boolean needToBuild) {
        this.buildRoot = Paths.get(sourceCodeProvider.locateSourceCode().toString(), gradleModule == null ? "" : gradleModule);

        var isJar = buildRoot.toString().endsWith(".jar");
        if (!isJar && sourceCodeProvider instanceof GradleBuildable) {
            if (needToBuild)
                GradleBuildManager.build(sourceCodeProvider.locateSourceCode().toString(), gradleModule);
            this.pathToJar = findJarInBuilds();
        } else pathToJar = buildRoot;
    }

    public JarSourceCodeProviderWrapper(SourceCodeProvider sourceCodeProvider, boolean needToBuild) {
        this(sourceCodeProvider, null, needToBuild);
    }

    @Override
    public Path locateSourceCode() throws SourceLoadingException {
        try {
            return getJarFileSystemPath(pathToJar);
        } catch (IOException e) {
            throw new SourceLoadingException(e);
        }
    }

    public Path getJarFileSystemPath(Path pathToJar) throws IOException {
        var uri = URI.create("jar:file:" + pathToJar.toUri().getPath());
        try {
            jarFileSystem = FileSystems.getFileSystem(uri);
        } catch (FileSystemNotFoundException e) {
            jarFileSystem = FileSystems.newFileSystem(uri, new HashMap<>());
        }
        return jarFileSystem.getPath("/");
    }

    // TODO exclude to gradle utils
    public Path findJarInBuilds() {
        try {
            var buildDir = Paths.get(buildRoot.toString(), "build", "libs");
            var jar = Files.find(buildDir, Integer.MAX_VALUE,
                    (path, attributes) -> path.toString().matches(".+all\\.jar"));
            return jar.findAny().orElseThrow();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void closeJarFileSystem() {
        try {
            jarFileSystem.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public Path getBuildRoot() {
        return buildRoot;
    }

    public Path getPathToJar() {
        return pathToJar;
    }

}
