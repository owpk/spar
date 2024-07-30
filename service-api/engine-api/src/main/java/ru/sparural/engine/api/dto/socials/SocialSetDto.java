package ru.sparural.engine.api.dto.socials;

import lombok.*;
import lombok.experimental.FieldDefaults;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Size;

/**
 * @author Vorobyev Vyacheslav
 */
@Getter
@Setter
@ToString
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class SocialSetDto {

    @NotEmpty(message = "Specify code")
    @Size(max = 100, message = "Maximum code length 100 characters")
    String code;
}
