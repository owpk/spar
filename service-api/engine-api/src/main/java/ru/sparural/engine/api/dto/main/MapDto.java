package ru.sparural.engine.api.dto.main;

import lombok.*;
import lombok.experimental.FieldDefaults;
import ru.sparural.engine.api.dto.merchant.Merchants;

import java.util.List;

/**
 * @author Vorobyev Vyacheslav
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class MapDto {
    String enterLongitude;
    String centerLatitude;
    Integer zoom;
    List<Merchants> merchants;
}
