package ru.sparural.gradle.plugins.kafka.client.model;


import ru.sparural.gradle.plugins.kafka.client.model.core.JavaClass;

public class KafkaClientAsyncMethod extends KafkaClientMethod {

    @Override
    public String getBegin() {
        return super.getBegin();
    }

    @Override
    public void setMethodName(String methodName) {
        super.setMethodName(methodName + "Async");
    }

    public void setReturnType(JavaClass completableFuture, JavaClass returnType) {
        this.returnType = JavaClass.builder()
                .packageName(completableFuture.getPackageName())
                .typeName(completableFuture.getTypeName())
                .diamondType(returnType)
                .build();
    }
}
