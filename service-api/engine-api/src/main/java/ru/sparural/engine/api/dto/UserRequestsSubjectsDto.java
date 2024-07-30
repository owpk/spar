package ru.sparural.engine.api.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;
import lombok.experimental.FieldDefaults;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Getter
@Setter
@ToString
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UserRequestsSubjectsDto {
    @JsonProperty
    Long id;
    @NotBlank(message = "Topic is empty")
    @Size(max = 255, message = "The maximum length of a topic is 255 characters")
    String name;
}
