package ru.sparural.engine.api.dto.goods;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;
import lombok.experimental.FieldDefaults;
import ru.sparural.engine.api.dto.FileDto;

@Getter
@Setter
@ToString
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class GoodsForAdminDto {
    Long id;
    String goodsId;
    String name;
    String description;
    @JsonIgnore
    Boolean draft;
    FileDto preview;
    FileDto photo;
}
