package ru.sparural.gradle.plugins.kafka.client.utils;

import ru.sparural.gradle.plugins.kafka.client.model.core.JavaMethod;
import ru.sparural.gradle.plugins.kafka.client.model.core.JavaVariable;

import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

public class JavaClassPrinter {

    public static String createField(JavaVariable javaVariable) {
        return createField(javaVariable.toView());
    }

    public static String createField(String variableView) {
        return appendTabs(1, "private " + variableView) + ";\n";
    }

    public static String appendTabs(int tabCount, String source) {
        return "\t".repeat(tabCount) + source;
    }

    public static String mapArgs(List<JavaVariable> args) {
        if (args != null)
            return buildArgs(args.stream().map(JavaVariable::toView).collect(Collectors.toList()));
        return "";
    }

    public static String buildArgs(List<String> args) {
        return String.join(", ", args);
    }

    public static boolean isMethodVoid(JavaMethod method) {
        return method.getReturnType().getSimpleName().equals("void");
    }

    public static String addImport(String fullClassName) {
        return "import " + fullClassName + ";\n";
    }

    public static String variableFromClassName(String className) {
        return className.substring(0, 1).toLowerCase(Locale.ROOT) + className.substring(1);
    }

    public static String classNameFromVariable(String variable) {
        return variable.substring(0, 1).toUpperCase(Locale.ROOT) + variable.substring(1);
    }

    public static String assignVariable(String var, String assignation) {
        return "this." + var + " = " + assignation + ";\n";
    }

}
