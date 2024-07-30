package ru.sparural.engine.api.dto.user;

import lombok.*;
import lombok.experimental.FieldDefaults;

@NoArgsConstructor
@Getter
@Setter
@ToString
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UserForCommentsDto {
    Long id;
    String firstName;
    String lastName;
    String patronymicName;
}
