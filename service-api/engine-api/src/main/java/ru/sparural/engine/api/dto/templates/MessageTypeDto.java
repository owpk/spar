package ru.sparural.engine.api.dto.templates;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@ToString
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class MessageTypeDto {
    Long id;
    String name;
}
