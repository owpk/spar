package ru.sparural.engine.api.dto.defaults;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.List;

/**
 * @author Vorobyev Vyacheslav
 */
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ValidationErrorsMessageDto extends MessageDto {

    List<ErrorEntry> errors;

    public ValidationErrorsMessageDto(String message, List<ErrorEntry> errors) {
        super(message);
        this.errors = errors;
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ErrorEntry {
        String fieldName;
        List<String> error;
    }
}
