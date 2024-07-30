package ru.sparural.gradle.plugins.kafka.client.core;

import ru.sparural.gradle.plugins.kafka.client.exceptions.SourceLoadingException;
import ru.sparural.gradle.plugins.kafka.client.model.KafkaClientMethod;

import java.util.ArrayDeque;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public abstract class AsyncKafkaControllersParser<T> extends KafkaControllersParser<T> {
    private static final Integer THREAD_COUNT = 5;

    public AsyncKafkaControllersParser(SourceExplorer<T> sourceExplorer,
                                       String kafkaControllerAnnotation, String kafkaMappingAnnotation) {
        super(sourceExplorer, kafkaControllerAnnotation, kafkaMappingAnnotation);
    }

    public Map<String, List<KafkaClientMethod>> parseKafkaMethods() throws SourceLoadingException {
        List<T> sourceCodeFiles = sourceExplorer.walkSource();
        Map<String, List<KafkaClientMethod>> result = new HashMap<>();
        var taskQueue = new ArrayDeque<Future<Map<String, List<KafkaClientMethod>>>>();
        var executor = Executors.newFixedThreadPool(THREAD_COUNT);

        sourceCodeFiles.forEach(file -> taskQueue.add(executor.submit(() -> processSourceCode(file))));

        while (!taskQueue.isEmpty()) {
            try {
                var methods = taskQueue.pop().get();
                result.putAll(methods);
            } catch (InterruptedException | ExecutionException e) {
                throw new RuntimeException(e);
            }
        }
        executor.shutdown();
        return result;
    }
}
