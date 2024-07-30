package ru.sparural;

import ru.sparural.app.controllers.ApplicationKafkaController;

public class Main {
    private static final ApplicationKafkaController controller = new ApplicationKafkaController();

    public static void main(String[] args) {
        controller.receiveSomeMessage("msg");
    }
}