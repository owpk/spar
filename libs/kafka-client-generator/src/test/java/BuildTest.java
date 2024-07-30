import ru.sparural.gradle.plugins.kafka.client.core.impl.provider.JarSourceCodeProviderWrapper;
import ru.sparural.gradle.plugins.kafka.client.core.impl.provider.SimpleFileSystemSourceProvide;
import ru.sparural.gradle.plugins.kafka.client.utils.GradleBuildManager;

public class BuildTest {
    public static void main(String[] args) {
        var provider = new SimpleFileSystemSourceProvide("/home/owpk/gh/spar/sparural-ecomm");
        var jarWrapper = new JarSourceCodeProviderWrapper(provider, "sparural-engine-service", true);
        var jar = jarWrapper.findJarInBuilds();
        System.out.println(jar);
    }
}
