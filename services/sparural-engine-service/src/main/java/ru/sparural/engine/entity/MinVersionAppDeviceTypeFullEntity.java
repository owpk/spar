package ru.sparural.engine.entity;

import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class MinVersionAppDeviceTypeFullEntity {
    private Long id;
    private String minVersionApp;
    private DeviceType deviceType;
}

