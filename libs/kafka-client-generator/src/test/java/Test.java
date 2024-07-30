import ru.sparural.gradle.plugins.kafka.client.core.SourceCodeWriter;
import ru.sparural.gradle.plugins.kafka.client.core.impl.explorer.jar.JarSourceExplorerImpl;
import ru.sparural.gradle.plugins.kafka.client.core.impl.generator.KafkaClientGeneratorImpl;
import ru.sparural.gradle.plugins.kafka.client.core.impl.generator.SourceCodeEntry;
import ru.sparural.gradle.plugins.kafka.client.core.impl.parser.reflection.JarSourceCodeParser;
import ru.sparural.gradle.plugins.kafka.client.core.impl.parser.reflection.hook.JdServiceApiClassInterceptorImp;
import ru.sparural.gradle.plugins.kafka.client.core.impl.provider.JarSourceCodeProviderWrapper;
import ru.sparural.gradle.plugins.kafka.client.core.impl.provider.SimpleFileSystemSourceProvide;

import java.io.IOException;
import java.nio.file.Paths;

public class Test {

    public static void main(String[] args) throws IOException {
        var provider = new SimpleFileSystemSourceProvide("/home/owpk/gh/spar/sparural-ecomm/sparural-engine-service/build/libs/sparural-engine-service-1.0.0-all.jar");
        var jarWrapper = new JarSourceCodeProviderWrapper(provider, false);
        var jarExplorer = new JarSourceExplorerImpl(jarWrapper);
        var apiInterceptor = new JdServiceApiClassInterceptorImp("engine");

        var parser = new JarSourceCodeParser(apiInterceptor, jarExplorer,
                "KafkaSparuralController",
                "KafkaSparuralMapping");

        var generator = new KafkaClientGeneratorImpl(parser, "engine", "org.owpk");
        var sources = generator.getEntriesToWrite();

        var apiServiceEntries = apiInterceptor.getEntriesToWrite();

        apiServiceEntries.forEach(Test::printSourceCode);
        sources.forEach(Test::printSourceCode);

        var writer = new SourceCodeWriter();
        writer.write(apiServiceEntries, Paths.get("src", "main", "java"));
    }

    private static void printSourceCode(SourceCodeEntry entry) {
        System.out.println(entry.getSourceCode());
    }

}