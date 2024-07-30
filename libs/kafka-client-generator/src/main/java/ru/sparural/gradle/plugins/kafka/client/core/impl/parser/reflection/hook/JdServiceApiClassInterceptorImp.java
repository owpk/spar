package ru.sparural.gradle.plugins.kafka.client.core.impl.parser.reflection.hook;

import org.jd.core.v1.ClassFileToJavaSourceDecompiler;
import ru.sparural.gradle.plugins.kafka.client.core.impl.generator.SourceCodeEntry;

import java.util.ArrayList;
import java.util.List;

public class JdServiceApiClassInterceptorImp implements ApiClassesInterceptor {
    private final String serviceApiName;
    private final ClassFileToJavaSourceDecompiler decompiler = new ClassFileToJavaSourceDecompiler();
    private final List<SourceCodeEntry> serviceApiEntries = new ArrayList<>();

    public JdServiceApiClassInterceptorImp(String serviceApiName) {
        this.serviceApiName = serviceApiName;
    }

    @Override
    public void acceptClass(Class<?> cl) {
        if (!cl.getPackageName().startsWith("java") &&
                cl.getPackageName().matches(".+" + serviceApiName + "\\.api\\..+")) {
            final var loader = new JdLoaderImpl(cl);
            final var printer = new JdPrinterImpl();
            try {
                printer.append("package ").append(cl.getPackageName()).append(";").append("\n\n");
                decompiler.decompile(loader, printer, cl.getName());
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
            serviceApiEntries.add(new SourceCodeEntry(cl.getPackageName(), cl.getSimpleName(), printer.toString()));
        }
    }

    @Override
    public List<SourceCodeEntry> getEntriesToWrite() {
        return serviceApiEntries;
    }
}
