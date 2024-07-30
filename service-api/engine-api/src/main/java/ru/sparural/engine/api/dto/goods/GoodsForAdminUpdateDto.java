package ru.sparural.engine.api.dto.goods;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;
import lombok.experimental.FieldDefaults;

import javax.validation.constraints.Size;

@Getter
@Setter
@ToString
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class GoodsForAdminUpdateDto {
    @JsonIgnore
    Long id;
    //TODO: реализовать логику "товара с таким идентификатором больше не существует"
    @Size(max = 100, message = "The maximum length of an id of goods is 100 characters")
    String goodsId;
    @Size(max = 100, message = "The maximum length of a name of goods is 100 characters")
    String name;
    @Size(max = 1000, message = "The maximum length of a description of goods is 100 characters")
    String description;
    Boolean draft;
}
