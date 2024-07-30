package ru.sparural.engine.loymax.rest.dto.offer;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.*;
import lombok.experimental.FieldDefaults;
import ru.sparural.engine.loymax.rest.dto.LoymaxImage;

import java.util.List;

@NoArgsConstructor
@Getter
@Setter
@ToString
@JsonIgnoreProperties(ignoreUnknown = true)
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Brand {
    String id;
    String name;
    List<LoymaxImage> images;
}
