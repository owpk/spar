package ru.sparural.engine.entity;

import lombok.Data;
import lombok.ToString;

/**
 * @author Vorobyev Vyacheslav
 */
@Data
@ToString
public class Registrations {
    private Long id;
    private Long userId;
    private Integer step;
}
