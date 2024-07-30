package ru.sparural.engine.entity;

import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class PushTokenReq {
    private String deviceType;
    private String token;
}
