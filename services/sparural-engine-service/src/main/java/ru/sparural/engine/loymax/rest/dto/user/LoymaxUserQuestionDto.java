package ru.sparural.engine.loymax.rest.dto.user;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
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
public class LoymaxUserQuestionDto {
    String type;
    String questionType;
    String logicalName;
    String regexp;
    String regexpErrorMessage;
    String isRequired;
    String isReadOnly;
    String isMultiSelect;
    List<LoymaxFixedAnswer> fixedAnswers;
    LoymaxUserAnswerDto answer;
}
