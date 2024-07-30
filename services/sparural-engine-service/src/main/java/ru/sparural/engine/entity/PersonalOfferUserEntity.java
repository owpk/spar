package ru.sparural.engine.entity;

import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class PersonalOfferUserEntity {
    private Long id;
    private Long userId;
    private Long personalOfferId;
    private String data;
}
