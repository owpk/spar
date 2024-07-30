package ru.sparural.engine.entity;

import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class Social {
    private Long id;
    private String name;
    private String appId;
    private String appSecret;
}
