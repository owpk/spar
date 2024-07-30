package ru.sparural.engine.api.dto;

import lombok.*;
import lombok.experimental.FieldDefaults;
import ru.sparural.engine.api.dto.main.CategoryDto;

import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CategoriesDto {
    List<CategoryDto> data;
    MetaDto meta;
}
