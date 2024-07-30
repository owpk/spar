package ru.sparural.engine.api.dto;

import lombok.*;
import lombok.experimental.FieldDefaults;
import ru.sparural.engine.api.validators.annotations.Url;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ExternalDocumentDto {

    Long id;

    @Pattern(regexp = "[a-zA-Z\\-_]+",
            message = "The alias can only consist of Latin characters, characters '_' and '-'")
    @Size(max = 100, message = "Maximum alias length 100 characters")
    @NotBlank(message = "The alias of the document must not be an empty string")
    @NotNull(message = "Please indicate the alias of the document")
    String alias;

    @Size(max = 100, message = "Maximum title length 100 characters")
    @NotBlank(message = "The title of the document must not be an empty string")
    @NotNull(message = "Please indicate the title of the document")
    String title;

    @Url
    String url;
}
