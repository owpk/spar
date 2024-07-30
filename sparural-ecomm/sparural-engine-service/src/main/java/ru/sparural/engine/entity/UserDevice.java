package ru.sparural.engine.entity;

import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class UserDevice {
    private Long id;
    private String identifier;
    private Long userId;
    private String data;
    private String versionApp;
}
