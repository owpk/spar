package ru.sparural.gradle.plugins.kafka.client.model;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import ru.sparural.gradle.plugins.kafka.client.model.core.JavaMethod;
import ru.sparural.gradle.plugins.kafka.client.model.core.JavaVariable;
import ru.sparural.gradle.plugins.kafka.client.model.core.Modifiers;

import java.util.ArrayList;
import java.util.List;

@FieldDefaults(level = AccessLevel.PRIVATE)
@Getter
@Setter
public class KafkaClientMethod extends JavaMethod {
    String action;
    JavaVariable payload;
    List<JavaVariable> requestParams = new ArrayList<>();

    public KafkaClientMethod() {
        this.modifier = Modifiers.PUBLIC;
    }

    public KafkaClientMethod addRequestParams(JavaVariable javaVariable) {
        requestParams.add(javaVariable);
        return this;
    }

    @Override
    public String toString() {
        return "KafkaClientMethod{" +
                "action='" + action + '\'' +
                ", payload=" + payload +
                ", requestParams=" + requestParams +
                ", exceptionThrows=" + exceptionThrows +
                ", isStatic=" + isStatic +
                ", methodName='" + methodName + '\'' +
                ", returnType=" + returnType +
                ", args=" + args +
                ", annotation='" + annotation + '\'' +
                ", modifier=" + modifier +
                '}';
    }

}
