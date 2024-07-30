package ru.sparural.engine.api.dto.request;

import lombok.*;
import lombok.experimental.FieldDefaults;
import ru.sparural.engine.api.dto.FileDto;
import ru.sparural.engine.api.dto.UserRequestsSubjectsDto;

import java.util.List;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
//@JsonInclude(JsonInclude.Include.NON_NULL)
public class UserRequestDto {
    Long id;
    UserProfileDto user;
    String fullName;
    String email;
    UserRequestsSubjectsDto subject;
    String message;
    List<FileDto> attachments;
}
