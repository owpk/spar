package ru.sparural.gradle.plugins.kafka.client.dsl;

import org.gradle.api.provider.Property;

public abstract class GitLoaderConfiguration {

    abstract public Property<String> getRemoteURI();

    abstract public Property<String> getRemoteBranch();

    abstract public Property<String> getUserName();

    abstract public Property<String> getPassword();
}
