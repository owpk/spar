package ru.sparural.gradle.plugins.kafka.client.core.impl.provider;

import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.transport.CredentialsProvider;
import ru.sparural.gradle.plugins.kafka.client.core.GradleBuildable;
import ru.sparural.gradle.plugins.kafka.client.exceptions.SourceLoadingException;
import ru.sparural.gradle.plugins.kafka.client.utils.KafkaClientFileUtils;

import java.nio.file.Path;

public class RemoteGitSourceCodeProvider
        extends AbstractSourceLoader implements GradleBuildable {

    private final CredentialsProvider credentials;
    private final Path tempDir;
    private final String projectUri;

    // TODO
    private final String branch;
    private final String remote;
    private final Path sourcePath;

    public RemoteGitSourceCodeProvider(CredentialsProvider credentials,
                                       String projectUrl, String branch, String remote) {

        this.credentials = credentials;
        this.tempDir = KafkaClientFileUtils.defineTempDir();
        this.projectUri = projectUrl;
        this.branch = branch;
        this.remote = remote;
        this.sourcePath = loadFromGit();
    }

    public RemoteGitSourceCodeProvider(CredentialsProvider credentials,
                                       String projectUrl, String branch) {
        this(credentials, projectUrl, branch, "origin");
    }

    @Override
    protected Path provideSource() {
        return sourcePath;
    }

    private Path loadFromGit() {
        try {
            var git = Git.cloneRepository()
                    .setRemote(remote)
                    .setCredentialsProvider(credentials)
                    .setDirectory(tempDir.toFile())
                    .setURI(projectUri)
                    .call();
            return git.getRepository()
                    .getDirectory().getParentFile().toPath();
        } catch (GitAPIException e) {
            throw new SourceLoadingException(e);
        }
    }

}