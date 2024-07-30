package ru.sparural.engine.api.dto;

import lombok.*;
import lombok.experimental.FieldDefaults;

import javax.validation.constraints.NotNull;

/**
 * @author Vorobyev Vyacheslav
 */
@Getter
@Setter
@ToString
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class RejectPaperCheckDto {

    @NotNull(message = "Please specify rejectPaperChecks value (true | false)")
    Boolean rejectPaperChecks;
}
