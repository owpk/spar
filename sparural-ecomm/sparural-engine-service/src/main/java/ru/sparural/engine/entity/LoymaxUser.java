package ru.sparural.engine.entity;

import lombok.Data;
import lombok.ToString;

/**
 * @author Vorobyev Vyacheslav
 */
@ToString
@Data
public class LoymaxUser {
    private Long id;
    private Long userId;
    private String token;
    private Long expiresAt;
    private String refreshToken;
    private String personUid;
    private Boolean setMobileApplicationInstalled;
    private Long loymaxUserId;
}
