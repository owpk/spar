package ru.sparural.gradle.plugins.kafka.client.model.core;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import ru.sparural.gradle.plugins.kafka.client.utils.JavaClassPrinter;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PROTECTED)
public class JavaClassDefinition extends JavaCodeElement {
    List<JavaClass> implementsNames = new ArrayList<>();
    Set<JavaClass> imports = new LinkedHashSet<>();
    JavaClass extendName;
    String packageName;
    String className;

    @Override
    public String getBegin() {
        var pck = "package " + packageName + ";\n\n";
        var imps = imports.stream()
                .sorted()
                .map(imp -> JavaClassPrinter.addImport(imp.getPackageName() + "." + imp.getSimpleName()))
                .collect(Collectors.joining()) + "\n";
        var implementations = !implementsNames.isEmpty() ?
                "implements " + implementsNames.stream().map(JavaClass::getCanonicalName)
                        .collect(Collectors.joining(",")) + " " : "";
        var classDef = modifier + "class " + className + " " + implementations + "{\n";
        return pck + imps + classDef;
    }

    @Override
    public String getEnd() {
        return "}";
    }

    public JavaConstructor addConstructor(List<JavaVariable> args) {
        return this.addConstructor(args, Modifiers.PUBLIC);
    }

    public JavaConstructor addConstructor(List<JavaVariable> args, Modifiers modifiers) {
        var constr = JavaConstructor.builder()
                .constructorName(className)
                .args(args)
                .build();
        constr.setModifier(modifiers);
        this.addBody(constr);
        return constr;
    }

    public JavaClassDefinition addFields(List<JavaVariable> fields, Modifiers modifier) {
        var codeSnipp = new JavaCodeSnippet();
        codeSnipp.setCodeSnippet(fields.stream().map(x -> modifier + (x.isFinal ? "final " : "") + x.toView() + ";\n")
                .collect(Collectors.joining()));
        this.addBody(codeSnipp);
        return this;
    }

    public JavaClassDefinition addFields(List<JavaVariable> fields) {
        return addFields(fields, Modifiers.PRIVATE);
    }

    public JavaClassDefinition generateSetter(JavaVariable field) {
        var setter = new JavaMethod();
        setter.setModifier(Modifiers.PUBLIC);
        setter.setMethodName("set" +
                JavaClassPrinter.classNameFromVariable(field.getVariableName()));
        setter.addMethodBody("this." + field.getVariableName() + " = " + field.getVariableName());
        setter.setArgs(List.of(field));
        setter.setReturnType(JavaClass.builder()
                .typeName("void").build());
        this.addBody(setter);
        return this;
    }

    public JavaClassDefinition generateSetters(List<JavaVariable> fields) {
        fields.forEach(this::generateSetter);
        return this;
    }

    public JavaClassDefinition generateGetter(JavaVariable field) {
        var getter = new JavaMethod();
        getter.setModifier(Modifiers.PUBLIC);
        getter.setMethodName("get" +
                JavaClassPrinter.classNameFromVariable(field.getVariableName()));
        getter.addMethodBody("return this." + field.getVariableName() + ";");
        getter.setArgs(List.of());
        getter.setReturnType(JavaClass.builder()
                .typeName(field.getType().getCanonicalName()).build());
        this.addBody(getter);
        return this;
    }

    public JavaClassDefinition generateLazyGetters(JavaVariable field, List<JavaVariable> args) {
        var getter = new JavaMethod();
        getter.setModifier(Modifiers.PUBLIC);
        getter.setSynchronized(true);
        getter.setMethodName("get" +
                JavaClassPrinter.classNameFromVariable(field.getVariableName()));
        getter.addMethodBody("if (this." + field.getVariableName() + " == null)\n" +
                "\tthis." + field.getVariableName() + " = " + "new " +
                field.getType().getCanonicalName() + "(" + args.stream()
                .map(JavaVariable::getVariableName)
                .collect(Collectors.joining(", ")) + ");\n" +
                "return this." + field.getVariableName() + ";\n"
        );
        getter.setArgs(List.of());
        getter.setReturnType(JavaClass.builder()
                .typeName(field.getType().getCanonicalName()).build());
        this.addBody(getter);
        return this;
    }

    public JavaClassDefinition generateGetters(List<JavaVariable> fields) {
        fields.forEach(this::generateGetter);
        return this;
    }

    public JavaClassDefinition addImplementation(JavaClass iFace) {
        this.implementsNames.add(iFace);
        return this;
    }

    public JavaClassDefinition addImport(JavaClass kafkaStarterApiIFace) {
        if (kafkaStarterApiIFace.getPackageName() != null
                && !kafkaStarterApiIFace.getPackageName().isBlank() &&
                !Lang.containsAny(kafkaStarterApiIFace.getSimpleName()))
            this.imports.add(kafkaStarterApiIFace);
        return this;
    }

    public JavaClassDefinition addImports(List<JavaClass> imports) {
        imports.forEach(this::addImport);
        return this;
    }

    public JavaMethod addMethod(Boolean isStatic, Modifiers modifiers, String name,
                                JavaClass returnType, List<JavaVariable> args,
                                JavaClass thows) {
        var method = new JavaMethod();
        method.setModifier(modifiers);
        method.setMethodName(name);
        method.setReturnType(returnType);
        method.setArgs(args);
        method.setExceptionThrows(thows);
        method.setStatic(isStatic);
        this.addBody(method);
        return method;
    }

    public JavaMethod addMethod(Modifiers modifiers, String name, JavaClass returnType,
                                List<JavaVariable> args) {
        return addMethod(false, modifiers, name, returnType, args, null);
    }

    public JavaMethod addMethod(Modifiers modifiers, String name,
                                JavaClass returnType) {
        return addMethod(false, modifiers, name, returnType, null, null);
    }

    public JavaMethod addVoidMethod(Modifiers modifiers, String name) {
        return addMethod(false, modifiers, name, JavaClass.builder().typeName("void").build(), null, null);
    }

    public JavaMethod addVoidMethod(Modifiers modifiers, String name,
                                    List<JavaVariable> args) {
        return addMethod(false, modifiers, name, JavaClass.builder().typeName("void").build(), args, null);
    }
}
