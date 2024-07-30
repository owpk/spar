package ru.sparural.engine.loymax.rest.dto.user;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.List;

/**
 * @author Vorobyev Vyacheslav
 */
@Getter
@Setter
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
@ToString
@FieldDefaults(level = AccessLevel.PRIVATE)
public class LoymaxUserAnswerDto {
    Long questionId;
    List<Long> fixedAnswerIds;
    String value;
    String tag;
    String questionGroupId;
}
