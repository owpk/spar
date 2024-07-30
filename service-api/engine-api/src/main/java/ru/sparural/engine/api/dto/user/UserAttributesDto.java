package ru.sparural.engine.api.dto.user;

import lombok.*;
import lombok.experimental.FieldDefaults;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UserAttributesDto {

    Long id;

    @NotNull(message = "имя атрибута не должно быть пустым")
    @Size(min = 2, max = 100, message = "Mинимальное кол-во симоволов 2, максимальное кол-во символов 100")
    String attributeName;

    @NotNull(message = "имя не должно быть пустым")
    @Size(min = 2, max = 100, message = "Mинимальное кол-во симоволов 2, максимальное кол-во символов 100")
    String name;
}
