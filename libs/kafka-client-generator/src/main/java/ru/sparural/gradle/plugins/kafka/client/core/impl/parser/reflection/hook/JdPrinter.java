package ru.sparural.gradle.plugins.kafka.client.core.impl.parser.reflection.hook;

import org.jd.core.v1.api.printer.Printer;

public interface JdPrinter extends Printer {
    JdPrinter append(String text);
}
