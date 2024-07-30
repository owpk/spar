package ru.sparural.triggerapi.dto;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.io.Serializable;

@Getter
@Setter
@ToString
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class MessageTemplatesUserDto implements Serializable {
    Long id;
    String firstName;
    String lastName;
    String phoneNumber;
    String email;
}
