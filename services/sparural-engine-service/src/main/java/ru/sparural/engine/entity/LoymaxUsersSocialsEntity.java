package ru.sparural.engine.entity;

import lombok.Data;
import lombok.ToString;

/**
 * @author Vorobyev Vyacheslav
 */
@ToString
@Data
public class LoymaxUsersSocialsEntity {
    private Long id;
    private Long userId;
    private String loymaxSocialUserId;
    private Long socialId;
}
