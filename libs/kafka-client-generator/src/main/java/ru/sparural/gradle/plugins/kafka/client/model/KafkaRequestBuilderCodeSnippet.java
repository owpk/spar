package ru.sparural.gradle.plugins.kafka.client.model;

import ru.sparural.gradle.plugins.kafka.client.model.core.JavaCodeSnippet;
import ru.sparural.gradle.plugins.kafka.client.utils.JavaClassPrinter;

import java.util.stream.Collectors;

import static ru.sparural.gradle.plugins.kafka.client.utils.JavaClassPrinter.appendTabs;

public class KafkaRequestBuilderCodeSnippet extends JavaCodeSnippet {

    public KafkaRequestBuilderCodeSnippet(Boolean isAsync, String kafkaBuilderVarName,
                                          String topicName, KafkaClientMethod kafkaClientMethod) {
        this.codeSnippet = isAsync ? includeKafkaCreatorCodeAsync(kafkaClientMethod, kafkaBuilderVarName, topicName) :
                includeKafkaCreatorCode(kafkaClientMethod, kafkaBuilderVarName, topicName);
    }

    private String includeKafkaCreatorCode(KafkaClientMethod kafkaClientMethod,
                                           String kafkaBuilderVarName, String topicName) {
        return includeKafkaCreatorCodeBase(kafkaClientMethod, kafkaBuilderVarName, topicName) +
                appendTabs(1, ".sendForEntity();\n");
    }

    private String includeKafkaCreatorCodeAsync(KafkaClientMethod kafkaClientMethod, String kafkaBuilderVarName, String topicName) {
        var isVoid = JavaClassPrinter.isMethodVoid(kafkaClientMethod);
        return includeKafkaCreatorCodeBase(kafkaClientMethod, kafkaBuilderVarName, topicName) +
                appendTabs(1, ".sendAsync()") +
                (isVoid ? ";\n" :
                        ".getFuture()\n" +
                                appendTabs(3, ".thenApply(resp -> (" +
                                        kafkaClientMethod.getReturnType().getCanonicalName()) + ") resp.getPayload());") +
                "\n";
    }

    private String includeKafkaCreatorCodeBase(KafkaClientMethod kafkaClientMethod,
                                               String kafkaBuilderVarName, String topicName) {
        return "return " + kafkaBuilderVarName + ".getKafkaRequestCreator().createRequestBuilder()\n" +
                appendTabs(1, ".withTopicName(" + topicName + ")\n" +
                        appendTabs(1, ".withAction(\"" + kafkaClientMethod.getAction() + "\")\n") +
                        kafkaClientMethod.getRequestParams()
                                .stream()
                                .map(p -> appendTabs(1, ".withRequestParameter(\"" + p.getVariableName() + "\", " + p.getVariableName() + ")\n"))
                                .collect(Collectors.joining())) +
                (kafkaClientMethod.getPayload() != null ?
                        appendTabs(1, ".withRequestBody(" + kafkaClientMethod.getPayload().getVariableName() + ")\n")
                        : "");
    }

    @Override
    public String getBegin() {
        return super.getBegin();
    }
}
