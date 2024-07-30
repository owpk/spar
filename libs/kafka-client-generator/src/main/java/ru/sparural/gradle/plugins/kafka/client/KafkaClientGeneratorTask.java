package ru.sparural.gradle.plugins.kafka.client;

import org.eclipse.jgit.transport.UsernamePasswordCredentialsProvider;
import org.gradle.api.DefaultTask;
import org.gradle.api.Project;
import org.gradle.api.tasks.TaskAction;
import ru.sparural.gradle.plugins.kafka.client.core.KafkaClientGenerator;
import ru.sparural.gradle.plugins.kafka.client.core.SourceCodeProvider;
import ru.sparural.gradle.plugins.kafka.client.core.SourceCodeWriter;
import ru.sparural.gradle.plugins.kafka.client.core.impl.explorer.jar.JarSourceExplorerImpl;
import ru.sparural.gradle.plugins.kafka.client.core.impl.generator.KafkaClientGeneratorImpl;
import ru.sparural.gradle.plugins.kafka.client.core.impl.parser.reflection.JarSourceCodeParser;
import ru.sparural.gradle.plugins.kafka.client.core.impl.parser.reflection.hook.JdServiceApiClassInterceptorImp;
import ru.sparural.gradle.plugins.kafka.client.core.impl.provider.JarSourceCodeProviderWrapper;
import ru.sparural.gradle.plugins.kafka.client.core.impl.provider.RemoteGitSourceCodeProvider;
import ru.sparural.gradle.plugins.kafka.client.core.impl.provider.SimpleFileSystemSourceProvide;
import ru.sparural.gradle.plugins.kafka.client.dsl.GenerationSource;
import ru.sparural.gradle.plugins.kafka.client.dsl.KafkaClientGeneratorExtension;

import javax.inject.Inject;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

public abstract class KafkaClientGeneratorTask extends DefaultTask {

    private final KafkaClientGeneratorExtension config;
    private final Project project;
    private final Path targetDir;
    private final SourceCodeWriter writer;

    @Inject
    public KafkaClientGeneratorTask(KafkaClientGeneratorExtension config, Project project) {
        this.config = config;
        this.project = project;
        this.targetDir = Paths.get(project.getProjectDir().getAbsolutePath(), "src", "main", "java");
        this.writer = new SourceCodeWriter();
    }

    @TaskAction
    public void generate() throws IOException {
        var sourceLoader = getSourceLoader(config.getSource());
        var jarSourceProviderWrapper = new JarSourceCodeProviderWrapper(sourceLoader,
                config.getSource().getModuleName().get(),
                config.getSource().getNeedToBuild().get());

        var jarExplorer = new JarSourceExplorerImpl(jarSourceProviderWrapper);

        var serviceApiInterceptor = new JdServiceApiClassInterceptorImp(config.getServiceName().get());

        var parser = new JarSourceCodeParser(serviceApiInterceptor, jarExplorer,
                config.getKafkaControllerAnnotationName().get(), config.getKafkaMappingAnnotationName().get());

        KafkaClientGenerator clientGenerator = new KafkaClientGeneratorImpl(
                parser,
                config.getServiceName().get(),
                project.getGroup().toString());

        var kafkaClientsEntries = clientGenerator.getEntriesToWrite();
        var apiSourceEntries = serviceApiInterceptor.getEntriesToWrite();
        kafkaClientsEntries.addAll(apiSourceEntries);

        writer.write(kafkaClientsEntries, targetDir);
    }

    private SourceCodeProvider getSourceLoader(GenerationSource sourceConfig) {
        if (sourceConfig.getLocalSource().isPresent()) {
            return new SimpleFileSystemSourceProvide(sourceConfig.getLocalSource().get());
        } else if (sourceConfig.getGitConfig() != null) {
            var gitCfg = sourceConfig.getGitConfig();
            var credentialsProvider = new UsernamePasswordCredentialsProvider(
                    gitCfg.getUserName().get(), gitCfg.getPassword().get());
            return new RemoteGitSourceCodeProvider(credentialsProvider, gitCfg.getRemoteURI().get(), gitCfg.getRemoteBranch().get());
        } else throw new RuntimeException("Source generation configuration not specified");
    }
}
