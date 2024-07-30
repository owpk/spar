package ru.sparural.gradle.plugins.kafka.client.core.impl.generator;

import ru.sparural.gradle.plugins.kafka.client.core.KafkaClientGenerator;
import ru.sparural.gradle.plugins.kafka.client.core.KafkaControllersParser;
import ru.sparural.gradle.plugins.kafka.client.model.*;
import ru.sparural.gradle.plugins.kafka.client.model.core.JavaClass;
import ru.sparural.gradle.plugins.kafka.client.model.core.JavaClassDefinition;
import ru.sparural.gradle.plugins.kafka.client.model.core.JavaVariable;
import ru.sparural.gradle.plugins.kafka.client.model.core.Modifiers;
import ru.sparural.gradle.plugins.kafka.client.model.core.visitors.JavaElementVisitorImpl;
import ru.sparural.gradle.plugins.kafka.client.utils.JavaClassPrinter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

import static ru.sparural.gradle.plugins.kafka.client.model.KafkaClientConstants.*;

public class KafkaClientGeneratorImpl extends KafkaClientGenerator {
    private static final String GENERATED_API_CLASS_NAME_SUFFIX = "KafkaClient";
    private final String serviceName;
    private final String basePackageName;
    private final String apisPackageName;
    private final List<JavaVariable> generatedApisFieldRepresentations = new ArrayList<>();

    public KafkaClientGeneratorImpl(KafkaControllersParser<?> kafkaControllersParser, String serviceName, String projectGroupName) throws IOException {
        super(kafkaControllersParser);
        this.serviceName = serviceName.trim().toLowerCase(Locale.ROOT);
        this.basePackageName = projectGroupName + ".kafka.client." + serviceName;
        this.apisPackageName = basePackageName + ".api";
    }

    @Override
    public SourceCodeEntry processKafkaMethods(String controllerClassName, List<KafkaClientMethod> kafkaMethods) {
        var className = controllerClassName + GENERATED_API_CLASS_NAME_SUFFIX;
        var controllerApi = new GeneratedApiClassDefinition(
                apisPackageName,
                className,
                kafkaRCVariable,
                reqTopicName,
                kafkaMethods
        );

        var args = List.of(kafkaRCVariable, reqTopicName);

        controllerApi.addFields(args)
                .addConstructor(args)
                .assignArgsToFields();

        kafkaMethods.forEach(method -> addBodyToKafkaMethod(controllerApi, method));

        var visitor = new JavaElementVisitorImpl();
        visitor.visit(controllerApi, 0);

        var apiVariable = JavaVariable.builder()
                .variableName(JavaClassPrinter.variableFromClassName(controllerApi.getClassName()))
                .type(JavaClass.builder()
                        .packageName(controllerApi.getPackageName())
                        .typeName(controllerApi.getClassName())
                        .build())
                .build();
        generatedApisFieldRepresentations.add(apiVariable);
        return new SourceCodeEntry(apisPackageName, className, visitor.getResult());
    }

    @Override
    protected List<SourceCodeEntry> addClassesToGenerate() {
        var apiObjFactory = createApiObjectFactory(basePackageName, serviceName, generatedApisFieldRepresentations);
        return List.of(apiObjFactory);
    }

    private void addBodyToKafkaMethod(JavaClassDefinition api, KafkaClientMethod method) {
        var isVoid = JavaClassPrinter.isMethodVoid(method);
        var asyncM = new KafkaClientAsyncMethod();
        asyncM.setMethodName(method.getMethodName());
        asyncM.setReturnType(complFuture, method.getReturnType());
        asyncM.setArgs(method.getArgs());
        asyncM.setBody(
                new KafkaRequestBuilderCodeSnippet(true,
                        kafkaRCVariable.getVariableName(),
                        reqTopicName.getVariableName(), method));
        asyncM.setModifier(Modifiers.PUBLIC);

        if (!isVoid) {
            method.setBody(
                    new KafkaRequestBuilderCodeSnippet(false,
                            kafkaRCVariable.getVariableName(),
                            reqTopicName.getVariableName(), method));
            api.addBody(method);
        } else {
            api.addImport(kafkaRequestInfo);
            asyncM.setReturnType(kafkaRequestInfo);
        }
        api.addBody(asyncM);
    }

    private SourceCodeEntry createApiObjectFactory(String packageName, String serviceName,
                                                   List<JavaVariable> apisList) {
        var cl = new GeneratedObjectFactory(packageName, serviceName,
                KafkaClientConstants.kafkaRCVariable, KafkaClientConstants.reqTopicName)
                .addImports(apisList.stream().map(JavaVariable::getType).collect(Collectors.toList()))
                .addImport(KafkaClientConstants.kafkaRCVariable.getType())
                .addFields(apisList);

        var args = List.of(KafkaClientConstants.kafkaRCVariable, KafkaClientConstants.reqTopicName);
        cl.addFields(args)
                .addConstructor(args)
                .assignArgsToFields();

        apisList.forEach(a -> cl.generateLazyGetters(a, args));
        var visitor = new JavaElementVisitorImpl();
        visitor.visit(cl, 0);
        var sourceCode = visitor.getResult();
        return new SourceCodeEntry(packageName, cl.getClassName(), sourceCode);
    }
}
