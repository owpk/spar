package ru.sparural.rest.api.trigger;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@ToString
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class MessageTypeRestDto {
    Long id;
    String name;
}
