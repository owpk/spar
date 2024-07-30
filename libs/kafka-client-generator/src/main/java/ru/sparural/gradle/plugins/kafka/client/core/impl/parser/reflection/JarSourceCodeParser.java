package ru.sparural.gradle.plugins.kafka.client.core.impl.parser.reflection;

import org.apache.commons.lang3.ClassUtils;
import ru.sparural.gradle.plugins.kafka.client.core.AsyncKafkaControllersParser;
import ru.sparural.gradle.plugins.kafka.client.core.impl.explorer.jar.JarSourceExplorerImpl;
import ru.sparural.gradle.plugins.kafka.client.core.impl.parser.reflection.hook.ApiClassesInterceptor;
import ru.sparural.gradle.plugins.kafka.client.model.KafkaClientMethod;
import ru.sparural.gradle.plugins.kafka.client.model.core.JavaClass;
import ru.sparural.gradle.plugins.kafka.client.model.core.JavaVariable;

import java.lang.annotation.Annotation;
import java.lang.reflect.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class JarSourceCodeParser extends AsyncKafkaControllersParser<Class<?>> {
    // TODO exclude to properties
    private static final String REQUEST_PARAM_ANNOTATION_NAME = "RequestParam";
    // TODO exclude to properties
    private static final String PAYLOAD_ANNOTATION_NAME = "Payload";
    private final ApiClassesInterceptor apiClassInterceptor;
    private final Function<Parameter, JavaVariable> defaultParameterMapper = (p) ->
            JavaVariable.buildJavaVariable(p.getName(), mapParameterType(p));

    public JarSourceCodeParser(ApiClassesInterceptor apiClassInterceptor,
                               JarSourceExplorerImpl jarSourceExplorer,
                               String kafkaControllerAnnotation,
                               String kafkaMappingAnnotation) {
        super(jarSourceExplorer, kafkaControllerAnnotation, kafkaMappingAnnotation);
        this.apiClassInterceptor = apiClassInterceptor;
    }

    @Override
    protected Map<String, List<KafkaClientMethod>> processSourceCode(Class<?> kafkaControllersSource) {
        return findAnnotatedController(kafkaControllersSource);
    }

    private Map<String, List<KafkaClientMethod>> findAnnotatedController(Class<?> clazz) {
        apiClassInterceptor.acceptClass(clazz);

        var classAnnotations = clazz.getDeclaredAnnotations();
        var result = new HashMap<String, List<KafkaClientMethod>>();

        for (Annotation classAnnotation : classAnnotations) {
            if (matchKafkaAnnotation(classAnnotation, kafkaControllerAnnotation)) {
                var methods = clazz.getMethods();
                var clientMethods = new ArrayList<KafkaClientMethod>();

                for (Method method : methods) {
                    var methodAnnotations = method.getDeclaredAnnotations();

                    for (Annotation methodAnnotation : methodAnnotations) {
                        if (matchKafkaAnnotation(methodAnnotation, kafkaMappingAnnotation)) {
                            var annotationValue = findAnnotationValue(methodAnnotation, "value");
                            var kafkaClientMethod = parseKafkaMethod(method, annotationValue);
                            clientMethods.add(kafkaClientMethod);
                        }
                    }
                    result.put(clazz.getSimpleName(), clientMethods);
                }
            }
        }
        return result;
    }

    private String findAnnotationValue(Annotation annotation, String valueName) {
        try {
            var annotationType = annotation.annotationType();
            var valueMethod = annotationType.getMethod(valueName);
            var annotationValue = valueMethod.invoke(annotation);
            return annotationValue.toString();
        } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
            return null;
        }
    }

    private KafkaClientMethod parseKafkaMethod(Method method, String action) {
        var clientMethod = new KafkaClientMethod();
        clientMethod.setAction(action);

        Class<?> returnType = method.getReturnType();
        Type genericReturynType = method.getGenericReturnType();

        parseParametrizedType(genericReturynType);

        if (ClassUtils.isPrimitiveOrWrapper(returnType))
            returnType = ClassUtils.primitiveToWrapper(returnType);

        clientMethod.setReturnType(buildJavaClass(returnType, genericReturynType));

        String name = method.getName();
        clientMethod.setMethodName(name);

        var args = method.getParameters();
        for (Parameter arg : args) {
            mapClientMethodArgs(arg, clientMethod);
        }
        return clientMethod;
    }

    private void mapClientMethodArgs(Parameter parameter, KafkaClientMethod clientMethod) {
        var annotationsCache = buildAnnotationCache(parameter);
        if (annotationsCache.containsKey(PAYLOAD_ANNOTATION_NAME)) {
            var requestParam = defaultParameterMapper.apply(parameter);
            clientMethod.setPayload(requestParam);
            clientMethod.addArg(requestParam);

            parseParametrizedType(parameter.getParameterizedType());
        } else if (annotationsCache.containsKey(REQUEST_PARAM_ANNOTATION_NAME)) {
            Function<Parameter, JavaVariable> mapper = (p) -> {
                var annotation = annotationsCache.get(REQUEST_PARAM_ANNOTATION_NAME);
                var annotationValue = findAnnotationValue(annotation, "name");
                String javaVariableName = annotationValue != null ? annotationValue : parameter.getName();
                return JavaVariable.buildJavaVariable(javaVariableName, mapParameterType(parameter));
            };
            var arg = mapper.apply(parameter);
            clientMethod.addArg(arg);
            clientMethod.addRequestParams(arg);

            parseParametrizedType(parameter.getParameterizedType());
        }
    }

    private void parseParametrizedType(Type type) {
        if (type instanceof ParameterizedType) {
            var arg = ((ParameterizedType) type).getActualTypeArguments();
            for (Type t : arg)
                parseParametrizedType(t);
        }
    }

    private Map<String, Annotation> buildAnnotationCache(Parameter parameter) {
        var paramAnnotations = parameter.getDeclaredAnnotations();
        return Stream.of(paramAnnotations)
                .collect(Collectors.toMap(a -> a.annotationType().getSimpleName(), Function.identity()));
    }

    private JavaClass mapParameterType(Parameter parameter) {
        Class<?> pc = parameter.getType();
        Type pt = parameter.getParameterizedType();
        return buildJavaClass(pc, pt);
    }

    private JavaClass buildJavaClass(Class<?> rawType, Type diamondType) {
        var javaCl = buildJavaClass(rawType);
        if (diamondType != null) {
            if (diamondType instanceof ParameterizedType) {
                Type parametrizedDiamondType = ((ParameterizedType) diamondType).getActualTypeArguments()[0];
                Class<?> rawDiamondType;
                if (parametrizedDiamondType instanceof ParameterizedType)
                    rawDiamondType = (Class<?>) ((ParameterizedType) parametrizedDiamondType).getRawType();
                else
                    rawDiamondType = (Class<?>) parametrizedDiamondType;
                javaCl.setDiamondType(buildJavaClass(rawDiamondType, parametrizedDiamondType));
            }
        }
        return javaCl;
    }

    private JavaClass buildJavaClass(Class<?> clazz) {
        var javaCl = JavaClass.builder().build();
        javaCl.setTypeName(clazz.getSimpleName());
        javaCl.setPackageName(clazz.getPackageName());
        return javaCl;
    }

    private boolean matchKafkaAnnotation(Annotation annotation, String targetName) {
        return annotation.annotationType().getName().matches(".*" + targetName + ".*");
    }

}
