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
public class Status {
    Integer currentValue;
    Integer leftUntilNextStatus;
    ClientStatus clientStatus;
    String nextStatus;
    String statusNextLeft;
}