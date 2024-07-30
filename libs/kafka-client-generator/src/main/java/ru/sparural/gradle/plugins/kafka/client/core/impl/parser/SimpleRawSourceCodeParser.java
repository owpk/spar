package ru.sparural.gradle.plugins.kafka.client.core.impl.parser;

import ru.sparural.gradle.plugins.kafka.client.core.KafkaControllersParser;
import ru.sparural.gradle.plugins.kafka.client.core.SourceExplorer;
import ru.sparural.gradle.plugins.kafka.client.model.KafkaClientMethod;
import ru.sparural.gradle.plugins.kafka.client.model.core.JavaClass;
import ru.sparural.gradle.plugins.kafka.client.model.core.JavaVariable;
import ru.sparural.gradle.plugins.kafka.client.model.core.Lang;
import ru.sparural.gradle.plugins.kafka.client.utils.Constants;

import java.util.*;
import java.util.function.BiPredicate;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * since this parser uses values from imports to define the model, you should not use
 * wildcard import tokens in your java source
 * <p>
 * For example:
 * <p>
 * import a.b.c.*;
 * <p>
 * should be refactored to:
 * <p>
 * import a.b.c.D;
 * import a.b.c.E;
 * <p>
 * Deprecated! Unused, prefer to use JarSourceCodeParser
 */
@Deprecated
public class SimpleRawSourceCodeParser extends KafkaControllersParser<String> {
    private final Pattern detectKafkaController;
    private final Pattern kafkaMethod;
    private final Pattern packageNamePattern = Pattern.compile("package\\s+(.+);");
    private final Pattern classNamePattern = Pattern.compile("public\\s+class\\s+(\\w+)\\s*\\{");

    public SimpleRawSourceCodeParser(SourceExplorer<String> sourceExplorer,
                                     String kafkaControllerAnnotation,
                                     String kafkaMappingAnnotation) {
        super(sourceExplorer, kafkaControllerAnnotation, kafkaMappingAnnotation);
        this.detectKafkaController = Pattern.compile(kafkaControllerAnnotation);
        // 1 - kafka action name
        // 2 - return type
        // 3 - method name
        // 4 - method args
        this.kafkaMethod = Pattern.compile(kafkaMappingAnnotation +
                //1                                                  //2                               //3                        //4
                "\\(\"(.*)\"\\)(?:.|\\n)*?public[\\s|\\n]+(\\w+[a-zA-Z<>]*)[\\s|\\n]+(\\w+)[\\s|\\n]*\\(((?:.|\\n)*?)\\)[\\s|\\n|\\w|,]*\\{");
    }

    @Override
    protected Map<String, List<KafkaClientMethod>> processSourceCode(String kafkaControllersFile) {
        var hm = new HashMap<String, List<KafkaClientMethod>>();
        hm.put(findControllerClassName(kafkaControllersFile), generateKafkaMethods(kafkaControllersFile));
        return hm;
    }

    public boolean isKafkaController(String source) {
        return detectKafkaController.matcher(source).find();
    }

    public String findControllerClassName(String source) {
        return findGroup(source, classNamePattern, 1);
    }

    public String findPackageName(String source) {
        return findGroup(source, packageNamePattern, 1);
    }

    public String findClassPackageByImport(String source, String className) {
        if (Lang.containsAny(className))
            return null;
        var matcher = Pattern
                .compile("import\\s(.+)\\." + className)
                .matcher(source);
        if (matcher.find()) {
            return matcher.group(1);
        }
        return null;
    }

    public List<KafkaClientMethod> generateKafkaMethods(String source) {
        source = source.replaceAll("\\r", "");
        var matcher = kafkaMethod.matcher(source);
        var result = new ArrayList<KafkaClientMethod>();
        while (matcher.find()) {
            var methodModel = new KafkaClientMethod();
            var action = matcher.group(1);
            var returnType = matcher.group(2);
            var methodName = matcher.group(3);
            var args = matcher.group(4);
            var parsedArgs = parseMethodArgs(source, args);
            methodModel.setArgs(parsedArgs);

            BiPredicate<JavaVariable, String> predicate = (var, str) -> var != null && var.getType() != null && var.getType().getAnnotation() != null &&
                    var.getType().getAnnotation().equals(str);

            // TODO вынести имена аннотаций в конфиг
            var payload = parsedArgs.stream().filter(arg -> predicate.test(arg, Constants.PAYLOAD)).findAny().orElse(null);
            var requestParams = parsedArgs.stream().filter(arg -> predicate.test(arg, Constants.REQUEST_PARAM)).collect(Collectors.toList());

            methodModel.setPayload(payload);
            methodModel.setRequestParams(requestParams);
            methodModel.setAction(action);
            methodModel.setMethodName(methodName);
            methodModel.setReturnType(defineClass(source, returnType));

            result.add(methodModel);
        }
        return result;
    }

    // TODO вынести имена аннотаций в конфиг
    private List<JavaVariable> parseMethodArgs(String source, String argsLine) {
        var argsArray = argsLine.split(",");
        return Arrays.stream(argsArray)
                .map(arg -> arg.replaceAll("[\t|\r|\n]+", "").trim())
                .filter(arg -> !arg.isBlank())
                .map(arg -> {
                    var words = arg.split("\\s+");
                    var variable = JavaVariable.builder().build();
                    String annotation = null;
                    for (String word : words) {
                        if (word.startsWith(Constants.PAYLOAD)) {
                            annotation = word;
                        } else if (word.startsWith(Constants.REQUEST_PARAM)) { // RequestParam may contain 'name' parameter
                            if (word.matches("@.+\\(\".+\"\\)")) {
                                var variableName = word.substring(0, word.lastIndexOf("\""))
                                        .substring(word.indexOf("\"") + 1);
                                variable.setVariableName(variableName);
                            }
                            annotation = word;
                        } else if (word.matches("[A-Z]+.*")) {
                            variable.setType(defineClass(source, word, annotation));
                        } else if (word.matches("[a-z]+.*")) {
                            if (variable.getVariableName() == null)
                                variable.setVariableName(word);
                        }
                    }
                    return variable;
                }).collect(Collectors.toList());
    }

    private JavaClass defineClass(JavaClass classDefinition, String source, String className, String annotation) {
        if (className == null || className.isBlank())
            return null;
        classDefinition.setAnnotation(annotation);
        if (className.contains("<") && className.contains(">")) {
            var matcher = Pattern.compile("<(.*)>").matcher(className);
            var matchResult = matcher.find();
            var diamondType = matcher.group(1);
            var actualName = className.substring(0, className.indexOf("<"));
            var classPackageName = findClassPackageByImport(source, actualName);
            classDefinition.setPackageName(classPackageName);
            classDefinition.setTypeName(actualName);
            classDefinition.setDiamondType(defineClass(JavaClass.builder().build(), source, diamondType, null));
        } else {
            classDefinition.setPackageName(findClassPackageByImport(source, className));
            classDefinition.setTypeName(className);
        }
        return classDefinition;
    }

    private JavaClass defineClass(String source, String className, String annotation) {
        return defineClass(JavaClass.builder().build(), source, className, annotation);
    }

    public JavaClass defineClass(String source, String className) {
        return defineClass(JavaClass.builder().build(), source, className, null);
    }

    private String findGroup(String source, Pattern pattern, Integer idx) {
        var matcher = pattern.matcher(source);
        if (matcher.find())
            return matcher.group(idx);
        throw new RuntimeException("No class definition found: " + pattern);
    }

}
