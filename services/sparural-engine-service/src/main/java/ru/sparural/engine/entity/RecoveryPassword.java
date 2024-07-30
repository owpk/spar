package ru.sparural.engine.entity;

import lombok.Data;
import lombok.ToString;

/**
 * @author Vorobyev Vyacheslav
 */
@Data
@ToString
public class RecoveryPassword {
    private Long id;
    private Long userId;
    private ConfirmCodesNotifier notifier;
    private String notifierIdentity;
    private String token;
    private Long expired;
    private String code;
    private Long notifierId;
}
