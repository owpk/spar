package ru.sparural.engine.entity;

import lombok.Data;
import lombok.ToString;

/**
 * @author Vorobyev Vyacheslav
 */
@Data
@ToString
public class MinVersionAppEntity {
    private Long id;
    private String minVersionApp;
}