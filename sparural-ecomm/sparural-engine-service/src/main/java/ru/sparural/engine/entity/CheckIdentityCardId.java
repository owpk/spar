package ru.sparural.engine.entity;

import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class CheckIdentityCardId {
    String identity;
    Long cardId;
}
