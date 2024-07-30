package ru.sparural.engine.api.dto;

import lombok.*;
import lombok.experimental.FieldDefaults;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

@Getter
@Setter
@ToString
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ContentDto {

    Long id;

    @NotBlank(message = "Content must not be blank")
    String content;

    @Pattern(regexp = "[A-za-z\\-_]+",
            message = "The alias can only consist of Latin characters, characters '_' and '-'")
    @Size(max = 100, message = "Maximum alias length 100 characters")
    @NotNull(message = "Please indicate the alias of the document")
    String alias;

    @Pattern(regexp = "[A-za-z\\-_]+",
            message = "The alias can only consist of Latin characters, characters '_' and '-'")
    @Size(max = 100, message = "Maximum title length 100 characters")
    @NotNull(message = "Please indicate the title of the document")
    String title;
}
