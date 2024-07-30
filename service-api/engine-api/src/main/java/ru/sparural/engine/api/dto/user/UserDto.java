package ru.sparural.engine.api.dto.user;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;
import lombok.experimental.FieldDefaults;
import ru.sparural.engine.api.dto.FileDto;

import java.util.List;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class UserDto {
    Long id;
    String firstName;
    String lastName;
    String password;
    String phoneNumber;
    String email;
    Boolean isDraft;
    List<RoleDto> roles;

    String patronymicName;
    String gender;
    Long birthday;

    Boolean draft;
    Boolean smsAllowed;
    Boolean emailAllowed;
    Boolean viberAllowed;
    Boolean whatsappAllowed;
    Boolean pushAllowed;
    Boolean rejectPaperChecks;
    FileDto photo;

}