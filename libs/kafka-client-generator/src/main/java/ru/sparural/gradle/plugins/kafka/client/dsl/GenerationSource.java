package ru.sparural.gradle.plugins.kafka.client.dsl;

import org.gradle.api.Action;
import org.gradle.api.provider.Property;
import org.gradle.api.tasks.Nested;

public abstract class GenerationSource {
    @Nested
    public abstract GitLoaderConfiguration getGitConfig();

    public abstract Property<String> getLocalSource();

    public abstract Property<Boolean> getNeedToBuild();

    public abstract Property<String> getModuleName();

    public void gitConfig(Action<? super GitLoaderConfiguration> action) {
        action.execute(getGitConfig());
    }

}
