package ru.sparural.engine.loymax.rest.dto.cards;


import lombok.*;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@ToString
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class LoymaxUserCardQrDto {
    Long id;
    String codeGeneratedDate;
    String code;
    String lifeTime;
}