package ru.sparural.engine.api.dto.request;

import lombok.*;
import lombok.experimental.FieldDefaults;
import ru.sparural.engine.api.dto.FileDto;
import ru.sparural.engine.api.enums.Genders;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UserProfileDto {
    String firstName;
    String lastName;
    String phoneNumber;
    String email;
    Boolean emailConfirmed;
    Genders gender;
    Long birthday;
    FileDto photo;
}
