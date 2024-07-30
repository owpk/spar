package ru.sparural.engine.loymax.rest.dto.merchant;

import lombok.*;
import lombok.experimental.FieldDefaults;
import ru.sparural.engine.api.dto.FileDto;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Attribute {
    Long id;
    String name;
    FileDto icon;
}