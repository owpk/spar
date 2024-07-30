package ru.sparural.engine.entity;

import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class PushSetting {
    private int frequency;
    private String firebaseProjectId;
    private String huaweiAppId;
    private String huaweiAppSecret;
}
