package ru.sparural.engine.entity;

import lombok.Data;

@Data
public class UserPushTokensEntity {
    private Long id;
    private Long userid;
    private Long devicetypeid;
    private String devicetype;
    private String token;
    private Long createdat;
    private Long updatedat;
}
