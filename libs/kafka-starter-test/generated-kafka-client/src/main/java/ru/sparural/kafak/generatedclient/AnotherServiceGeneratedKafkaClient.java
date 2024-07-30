package ru.sparural.kafak.generatedclient;

import ru.sparural.kafka.client.api.KafkaProducer;

public class AnotherServiceGeneratedKafkaClient {

    private final KafkaProducer reqestCreator;

    public AnotherServiceGeneratedKafkaClient(KafkaProducer requestCreator) {
        this.reqestCreator = requestCreator;
    }

    public String checkTime() {
        var response = reqestCreator.send("", "/check_time");
        //... business logic
        return "RESULT: ....some logic result plus another service response... " + response;
    }
}
