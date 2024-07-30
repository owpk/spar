package ru.sparural.engine.loymax.rest.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import ru.sparural.engine.loymax.rest.dto.error.LoymaxValidationError;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
@FieldDefaults(level = AccessLevel.PRIVATE)
public class LoymaxResultResponse {
    @JsonProperty
    Integer code;
    @JsonProperty
    String state;
    @JsonProperty
    String message;
    @JsonProperty
    String messageCode;
    @JsonProperty
    String exception;
    @JsonProperty
    Body body;
    @JsonProperty
    List<LoymaxValidationError> validationErrors;

    @Override
    public String toString() {
        return "Loymax response message: [" +
                "state='" + state + '\'' +
                ", message='" + message + '\'' +
                ", messageCode='" + messageCode + '\'' +
                ", exception='" + exception + '\'' +
                ", validationErrors='" + validationErrors + '\'' +
                "]";
    }
}
