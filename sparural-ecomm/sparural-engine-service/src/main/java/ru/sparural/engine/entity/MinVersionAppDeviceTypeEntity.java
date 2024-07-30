package ru.sparural.engine.entity;

import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class MinVersionAppDeviceTypeEntity {
    private Long id;
    private String minVersionApp;
    private Long deviceTypeId;
    private String marketUrl;
}
