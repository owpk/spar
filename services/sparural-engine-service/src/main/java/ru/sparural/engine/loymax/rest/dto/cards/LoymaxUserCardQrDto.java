package ru.sparural.engine.loymax.rest.dto.cards;


import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
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