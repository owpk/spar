package ru.sparural.engine.services.exception;

import lombok.Getter;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Getter
public class ValidationException extends StatusException {
    private static final int DEFAULT_STATUS = CodeConstants.VALIDATION_ERROR;

    private Map<String, List<String>> errors;

    public ValidationException(Map<String, List<String>> errors) {
        super(DEFAULT_STATUS);
        this.errors = errors;
    }

    public ValidationException(List<String> error) {
        this(new HashMap<>());
        errors.put("validation error", error);
    }

    public ValidationException(String error) {
        super(error, DEFAULT_STATUS);
    }

    public ValidationException() {
        this(Collections.emptyMap());
    }

    public String collectErrors() {
        return errors.entrySet().stream().map(entrySet -> entrySet.getKey() + " : " +
                        String.join(", ", entrySet.getValue())).
                collect(Collectors.joining(",", "[", "]"));

    }
}
