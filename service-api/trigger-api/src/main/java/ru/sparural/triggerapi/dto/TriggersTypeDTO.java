package ru.sparural.triggerapi.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.io.Serializable;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class TriggersTypeDTO implements Serializable {
    @JsonProperty
    Long id;
    String code;
    String name;
}
