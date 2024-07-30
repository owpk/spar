package ru.sparural.engine.entity;

import lombok.Data;
import lombok.ToString;
import ru.sparural.engine.entity.enums.CardStatuses;

/**
 * @author Vorobyev Vyacheslav
 */
@Data
@ToString
public class Card {
    private Long id;
    private CardStatuses status;
    private String number;
    private String barCode;
    private Boolean block;
    private Long expiryDate;
    private Long ownerId;
    private String cardType;
}
