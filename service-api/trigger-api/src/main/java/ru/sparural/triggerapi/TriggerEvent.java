package ru.sparural.triggerapi;

import lombok.*;
import ru.sparural.triggerapi.dto.TriggersTypeDTO;

import java.io.Serializable;

/**
 * @author Vorobyev Vyacheslav
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class TriggerEvent implements Serializable {
    private Object body;
    private TriggersTypeDTO eventType;
}
