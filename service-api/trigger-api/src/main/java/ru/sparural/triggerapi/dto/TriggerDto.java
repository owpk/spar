package ru.sparural.triggerapi.dto;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.io.Serializable;

@ToString
@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class TriggerDto implements Serializable {
    Long id;
    TriggersTypeDTO triggerType;
    Long dateStart;
    Long dateEnd;
    Integer frequency;
    String timeStart;
    String timeEnd;
    String timeUnit;
}
