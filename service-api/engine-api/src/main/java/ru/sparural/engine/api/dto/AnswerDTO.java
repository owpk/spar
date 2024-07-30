package ru.sparural.engine.api.dto;


import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;
import lombok.experimental.FieldDefaults;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class AnswerDTO {
    @JsonProperty
    Long id;
    @NotBlank(message = "Answer is empty")
    @Size(max = 255, message = "The maximum length of an answer is 255 characters")
    String answer;
}
