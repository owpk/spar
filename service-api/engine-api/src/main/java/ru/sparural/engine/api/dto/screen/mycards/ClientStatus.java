package ru.sparural.engine.api.dto.screen.mycards;

import lombok.*;
import lombok.experimental.FieldDefaults;

/**
 * @author Vorobyev Vyacheslav
 */
@Getter
@Setter
@ToString
@FieldDefaults(level = AccessLevel.PRIVATE)
@NoArgsConstructor
public class ClientStatus {
    Long id;
    String name;
    Integer threshold;
}
