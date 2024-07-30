package ru.sparural.engine.api.dto.user;

import lombok.*;
import lombok.experimental.FieldDefaults;
import ru.sparural.engine.api.dto.FileDto;
import ru.sparural.engine.api.enums.Genders;

/**
 * @author Vorobyev Vyacheslav
 */
@NoArgsConstructor
@Getter
@Setter
@ToString
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UserProfileDto {
    Long id;
    String firstName;
    String lastName;
    String phoneNumber;
    String email;
    Boolean smsAllowed;
    Boolean emailAllowed;
    Boolean viberAllowed;
    Boolean whatsappAllowed;
    Boolean pushAllowed;
    Boolean rejectPaperChecks;
    Genders gender;
    Long birthday;

    Boolean emailConfirmed;
    FileDto photo;
    String unconfirmedEmail;
    String unconfirmedPhone;
}
