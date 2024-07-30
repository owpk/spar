package ru.sparural.engine.loymax.rest.dto.attribute;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
@FieldDefaults(level = AccessLevel.PRIVATE)
public class LoymaxUserAttributeInfoDto {
    Long id;
    String name;
    Integer order;
    String logicalName;
    Boolean historyIsRecorded;
}
