package ru.sparural.engine.api.dto;


import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;
import lombok.experimental.FieldDefaults;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class FaqDTO {
    @JsonProperty
    Long id;
    @NotBlank(message = "Question is empty")
    @Size(max = 1000, message = "The maximum length of an question is 1000 characters")
    String question;

    @NotBlank(message = "Answer is empty")
    @Size(max = 1000, message = "The maximum length of an answer is 1000 characters")
    String answer;
    @Min(value = 0, message = "The minimum value of a order is 0")
    @Max(value = 100, message = "The maximum value of a order is 100")
    Integer order;
}