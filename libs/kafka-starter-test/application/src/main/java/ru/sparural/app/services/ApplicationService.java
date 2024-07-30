package ru.sparural.app.services;

import ru.sparural.kafak.generatedclient.AnotherServiceGeneratedKafkaClient;
import ru.sparural.kafka.starter.RequestCreatorImpl;

public class ApplicationService {
    private final RequestCreatorImpl requestCreator = new RequestCreatorImpl();
    private final AnotherServiceGeneratedKafkaClient anotherServiceKafkaClient
            = new AnotherServiceGeneratedKafkaClient(requestCreator);

    public void doSomething() {
        var result = anotherServiceKafkaClient.checkTime();
        System.out.println("RESPONSE FROM ANOTHER SERVICE: " + result);
    }
}
