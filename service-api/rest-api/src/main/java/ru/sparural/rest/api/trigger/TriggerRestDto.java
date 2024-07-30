package ru.sparural.rest.api.trigger;

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
public class TriggerRestDto implements Serializable {
    Long id;
    TriggersTypeRestDto triggerType;
    Long dateStart;
    Long dateEnd;
    Integer frequency;
    String timeStart;
    String timeEnd;
    String timeUnit;
}
