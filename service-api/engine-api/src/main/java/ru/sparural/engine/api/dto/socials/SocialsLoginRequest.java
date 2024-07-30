package ru.sparural.engine.api.dto.socials;

import lombok.*;
import lombok.experimental.FieldDefaults;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

/**
 * @author Vorobyev Vyacheslav
 */
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class SocialsLoginRequest {
    @NotNull(message = "Specify code")
    @Size(max = 100, message = "Maximum code length 100 characters")
    String code;
}