package ru.sparural.triggerapi.dto;

import lombok.Getter;
import lombok.Setter;
import ru.sparural.triggerapi.UserEvent;

import java.io.Serializable;

/**
 * @author Vorobyev Vyacheslav
 */
@Getter
@Setter
public class UserTriggerEvent implements Serializable {
    private UserEvent body;
    private TriggerRequestDto trigger;
}