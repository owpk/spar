package ru.sparural.engine.entity;

import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class Content {
    private Long id;
    private String alias;
    private String title;
    private String content;
}
