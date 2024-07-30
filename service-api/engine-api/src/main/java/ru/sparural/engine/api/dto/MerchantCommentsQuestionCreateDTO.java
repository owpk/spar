package ru.sparural.engine.api.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;
import lombok.experimental.FieldDefaults;
import ru.sparural.engine.api.validators.annotations.ValidateQuestionType;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class MerchantCommentsQuestionCreateDTO {
    @JsonProperty

    @NotBlank(message = "Code is empty")
    @Size(max = 100, message = "The maximum length of a code is 100 characters")
    @Pattern(regexp = "^[a-z]*$",
            message = "The code can only consist of lowercase Latin characters")
    String code;
    @NotBlank(message = "Question is empty")
    @Size(max = 255, message = "The maximum length of a question code is 255 characters")
    String question;
    @Pattern(regexp = "^\\[\\d+(?:,\\d+)*\\]$",
            message = "The grade can only consist of integer value and be single or as an array\n" +
                    "For example: [value1, value2,..,valueN]")
    String grade;
    @ValidateQuestionType
    String type;
}