package ru.sparural.utils.rest;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * @author Vorobyev Vyacheslav
 */
public class JacksonObjectMapperUtils {
    private static final Map<String, TYPE> enumMap = Arrays.stream(TYPE.values())
            .collect(Collectors.toMap(x -> x.simpleName, Function.identity()));

    private final ObjectMapper objectMapper;

    public JacksonObjectMapperUtils() {
        objectMapper = new ObjectMapper();
    }

    public JacksonObjectMapperUtils(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    public String writeValueToString(Object value) {
        try {
            return objectMapper.writeValueAsString(value);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    public <X> byte[] convertToByte(X entity) {
        if (entity == null) return new byte[0];
        var fromType = entity.getClass().getName();
        var type = enumMap.get(fromType);
        if (type == null)
            type = TYPE.OBJECT;
        switch (type) {
            case BYTE:
                return (byte[]) entity;
            case STRING:
                return ((String) entity).getBytes(StandardCharsets.UTF_8);
            case OBJECT:
                try {
                    return objectMapper.writeValueAsBytes(entity);
                } catch (JsonProcessingException e) {
                    e.printStackTrace();
                }
        }
        return new byte[0];
    }

    public <X, C> C convert(X entity, TypeReference<C> target) {
        return defaultConvert(entity, null, target);
    }

    public <X, C> C convert(X entity, Class<C> target) {
        return defaultConvert(entity, target, null);
    }

    private <C, T> C defaultConvert(T from, Class<C> cl, TypeReference<C> typeReference) {
        var type = enumMap.get(from.getClass().getName());
        try {
            switch (type) {
                case STRING:
                    return cl != null ? objectMapper.readValue((String) from, cl)
                            : objectMapper.readValue((String) from, typeReference);
                case BYTE:
                    return cl != null ? objectMapper.readValue((byte[]) from, cl)
                            : objectMapper.readValue((byte[]) from, typeReference);
                default:
                    return cl != null ? objectMapper.convertValue(from, cl)
                            : objectMapper.convertValue(from, typeReference);
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("cannot process response body");
        }
    }

    private enum TYPE {
        STRING(String.class.getName()),
        BYTE(byte[].class.getName()),
        OBJECT(Object.class.getName());
        private final String simpleName;

        TYPE(String simpleName) {
            this.simpleName = simpleName;
        }
    }

}
