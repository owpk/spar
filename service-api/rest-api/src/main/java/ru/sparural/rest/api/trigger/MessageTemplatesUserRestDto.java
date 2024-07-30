package ru.sparural.rest.api.trigger;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@ToString
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class MessageTemplatesUserRestDto {
    Long id;
    String firstName;
    String lastName;
    String phoneNumber;
    String email;
}
