import org.eclipse.jgit.transport.UsernamePasswordCredentialsProvider;
import ru.sparural.gradle.plugins.kafka.client.core.impl.provider.JarSourceCodeProviderWrapper;
import ru.sparural.gradle.plugins.kafka.client.core.impl.provider.RemoteGitSourceCodeProvider;

public class BuildFromGitTest {

    public static void main(String[] args) {
        var provider = new RemoteGitSourceCodeProvider(
                new UsernamePasswordCredentialsProvider("owpk", "178UeeE34+"), "https://dev02.ctmol.ru/ecomm/sparural-ecomm.git", "master");

        var jarWrapper = new JarSourceCodeProviderWrapper(provider, "sparural-trigger", true);
        var jar = jarWrapper.findJarInBuilds();
        System.out.println(jar);
    }
}
