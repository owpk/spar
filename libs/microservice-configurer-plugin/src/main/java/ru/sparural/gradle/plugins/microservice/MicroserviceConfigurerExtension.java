package ru.sparural.gradle.plugins.microservice;

import com.bmuschko.gradle.docker.DockerSpringBootApplication;
import com.bmuschko.gradle.docker.DockerSpringBootApplicationPlugin;
import com.bmuschko.gradle.docker.tasks.image.Dockerfile;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import org.apache.commons.lang3.StringUtils;
import org.gradle.api.Project;
import org.gradle.api.plugins.ExtensionAware;
import org.gradle.api.publish.internal.DefaultPublishingExtension;
import org.gradle.api.publish.maven.MavenPublication;
import org.gradle.api.publish.maven.plugins.MavenPublishPlugin;
import ru.sparural.gradle.plugins.microservice.utils.DockerShellEntryPointInstruction;
import ru.sparural.gradle.plugins.microservice.utils.JooqLiquibaseGenerator;
import ru.sparural.gradle.plugins.microservice.utils.ProjectVersionChecker;

public class MicroserviceConfigurerExtension {

    private final Project project;

    public MicroserviceConfigurerExtension(Project project) {
        this.project = project;
        project.task("printProjectName").doLast(action -> {
            System.out.println(project.getName());
        });
    }

    public JooqLiquibaseGenerator databaseJooq() throws Exception {
        project.getDependencies().add("implementation", "org.jooq:jooq:3.16.5");
        return new JooqLiquibaseGenerator(project);
    }

    public MicroserviceConfigurerExtension gitVersion() throws Exception {
        String version = new ProjectVersionChecker(project).getProjectVersion();
        project.setVersion(version);
        project.task("printReleaseVersion").doLast(action -> {
            System.out.println(version);
        });
        return this;
    }

    public MicroserviceConfigurerExtension nexus() throws Exception {
        project.getPlugins().apply(MavenPublishPlugin.class);
        DefaultPublishingExtension extension = (DefaultPublishingExtension) project.getExtensions().getByName("publishing");

        if (StringUtils.isNoneEmpty(System.getenv("NEXUS_URI"))) {
            URI nexusRepo = new URI(System.getenv("NEXUS_URI"));
            extension.repositories(configure -> {
                configure.maven(action -> {
                    action.setName("Nexus");
                    action.setUrl(nexusRepo);
                    action.setAllowInsecureProtocol(true);
                    action.credentials(credentions -> {
                        credentions.setUsername(System.getenv("NEXUS_CREDENTIALS_USR"));
                        credentions.setPassword(System.getenv("NEXUS_CREDENTIALS_PSW"));
                    });
                });
                configure.mavenLocal();
            });
        }

        extension.repositories(configure -> {
            configure.mavenLocal();
        });

        extension.publications(configure -> {
            MavenPublication publication = configure.create("Spar", MavenPublication.class);
            publication.from(project.getComponents().getByName("java"));
        } );
        return this;
    }

    public MicroserviceConfigurerExtension docker() throws Exception {
        project.getPlugins().apply(DockerSpringBootApplicationPlugin.class);
        ExtensionAware extension = (ExtensionAware) project.getExtensions().getByName("docker");
        DockerSpringBootApplication appExtension = extension.getExtensions().getByType(DockerSpringBootApplication.class);
        appExtension.getJvmArgs().add("$JAVA_OPTS");
        appExtension.getPorts().empty();
        
        String javaVersion = System.getProperty("java.version").split("\\.")[0];
        appExtension.getBaseImage().set("openjdk:" + javaVersion);

        project.getTasksByName("dockerCreateDockerfile", false).forEach(task -> {
            task.doFirst(action -> {
                Dockerfile dockerfile = (Dockerfile) task;
                int idx;
                List<Dockerfile.Instruction> instuctions = new ArrayList<>(dockerfile.getInstructions().get());
                Dockerfile.EntryPointInstruction oldInstruction = null;
                for (idx=0; idx<instuctions.size(); idx++) {
                    if (instuctions.get(idx).getKeyword().equals(DockerShellEntryPointInstruction.KEYWORD)) {
                        oldInstruction = (Dockerfile.EntryPointInstruction) instuctions.get(idx);
                        break;
                    }
                }

                instuctions.set(idx, new DockerShellEntryPointInstruction(oldInstruction));
                dockerfile.getInstructions().set(instuctions);
                dockerfile.arg("GIT_COMMIT_HASH");
                dockerfile.arg("APPLICATION_VERSION");
            });
        });
        return this;
    }

}
