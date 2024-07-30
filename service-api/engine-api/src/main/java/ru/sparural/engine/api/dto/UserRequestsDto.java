package ru.sparural.engine.api.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;
import lombok.experimental.FieldDefaults;
import ru.sparural.engine.api.validators.annotations.ValidateUserRequests;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.List;

@Getter
@Setter
@ToString
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@ValidateUserRequests
public class UserRequestsDto {
    Long id;
    @Size(max = 100, message = "Maximum name length 100 characters")
    String fullName;
    @JsonIgnore
    Long userId;
    @Size(max = 100, message = "Maximum email length 100 characters")
    @Email(regexp = ".+[@].+[\\.].+", message = "Incorrect email")
    String email;
    @NotNull
    Long subjectId;
    @Size(max = 1000, message = "Maximum message length 1000 characters")
    String message;
    Boolean draft;
    List<FileDto> attachments;
}
