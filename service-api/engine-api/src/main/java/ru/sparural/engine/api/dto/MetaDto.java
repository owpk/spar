package ru.sparural.engine.api.dto;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class MetaDto {
    Long maxCount;
    Boolean accepted = false;
    Integer month;
    Integer year;
}
