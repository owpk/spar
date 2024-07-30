package ru.sparural.engine.entity;

import lombok.AccessLevel;
import lombok.Data;
import lombok.ToString;
import lombok.experimental.FieldDefaults;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
@ToString
//TODO Completely done when there is a file system
public class DeliveryEntity {
    private Long id;
    private String title;
    private String shortDescription;
    private String url;
    private boolean isPublic;
    private boolean draft;
}
