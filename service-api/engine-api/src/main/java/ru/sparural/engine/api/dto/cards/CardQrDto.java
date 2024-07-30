package ru.sparural.engine.api.dto.cards;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.*;
import lombok.experimental.FieldDefaults;


@Getter
@Setter
@ToString
@NoArgsConstructor
@JsonIgnoreProperties("cardId")
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CardQrDto {
    Long id;
    Long cardId;
    Long codeGeneratedDate;
    String code;
    Long lifeTime;
}