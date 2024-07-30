package ru.sparural.app.controllers;

import ru.sparural.app.services.ApplicationService;

public class ApplicationKafkaController {
    private final ApplicationService service = new ApplicationService();

    public void receiveSomeMessage(String param) {
        // receive kafka msg and send kafka message to another service...
        service.doSomething();
    }
}
