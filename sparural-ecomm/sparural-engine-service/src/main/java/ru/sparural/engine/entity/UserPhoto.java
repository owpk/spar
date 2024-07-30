package ru.sparural.engine.entity;

import lombok.Data;
import lombok.ToString;

/**
 * @author Vorobyev Vyacheslav
 */
@ToString
@Data
public class UserPhoto {
    private String uuid;
    private String name;
    private String ext;
    private Long size;
    private String mime;
    private String url;
}
