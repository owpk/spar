package ru.sparural.engine.loymax.rest.dto.counter;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
@FieldDefaults(level = AccessLevel.PRIVATE)
@ToString
public class LoymaxCounterResponse {
    List<String> hashes;
    String updateDate;
    TargetValues targetValues;
    String value;
}
