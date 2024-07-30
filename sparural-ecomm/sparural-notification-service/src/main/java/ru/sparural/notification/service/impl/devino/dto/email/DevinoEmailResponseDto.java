package ru.sparural.notification.service.impl.devino.dto.email;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.List;

/**
 * @author Vorobyev Vyacheslav
 */
@Getter
@Setter
@ToString
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@JsonIgnoreProperties(ignoreUnknown = true)
public class DevinoEmailResponseDto {

    @JsonProperty("Result")
    List<Result> result;

    @JsonProperty("Code")
    String code;

    @JsonProperty("Description")
    String description;
}
