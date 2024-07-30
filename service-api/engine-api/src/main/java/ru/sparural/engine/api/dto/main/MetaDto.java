package ru.sparural.engine.api.dto.main;

import lombok.*;
import lombok.experimental.FieldDefaults;
import ru.sparural.engine.api.dto.MainBlockDto;

import java.util.List;

@Getter
@Setter
@ToString
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class MetaDto {
    List<MainBlockDto> mainBlocks;
}
