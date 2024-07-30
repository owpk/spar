package ru.sparural.engine.api.dto.counters;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.*;
import lombok.experimental.FieldDefaults;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@JsonIgnoreProperties(ignoreUnknown = true)
public class CounterDto {
    Long id;

    @NotNull(message = "имя счетчика не должно быть пустым")
    @Size(min = 2, max = 100, message = "Mинимальное кол-во симоволов 2, максимальное кол-во символов 100")
    String name;

    @NotNull(message = "id счетчика не должно быть пустым")
    @Size(min = 2, max = 100, message = "Mинимальное кол-во симоволов 2, максимальное кол-во символов 100")
    String loymaxId;

    Integer value;
}
