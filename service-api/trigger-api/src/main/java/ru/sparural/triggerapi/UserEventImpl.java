package ru.sparural.triggerapi;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.util.List;

/**
 * @author Vorobyev Vyacheslav
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class UserEventImpl implements UserEvent, Serializable {
    private Long initialTimestamp;
    private Long userId;
}
