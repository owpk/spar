package ru.sparural.gradle.plugins.kafka.client.model.core;

import lombok.Getter;

public enum Modifiers {
        PUBLIC("public "),
        PRIVATE("private "),
        PROTECTED("protected "),
        EMPTY("");

        @Getter
        private final String modName;

        Modifiers(String modName) {
            this.modName = modName;
        }

        @Override
        public String toString() {
            return modName;
        }
    }
