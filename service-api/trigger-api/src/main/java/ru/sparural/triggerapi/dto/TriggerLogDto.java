package ru.sparural.triggerapi.dto;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.io.Serializable;

@Getter
@Setter
@ToString
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class TriggerLogDto implements Serializable {
    Long id;
    Long triggersDocumentId;
    Long triggersDocumentTypeId;
    Long triggersTypeId;
    Long datetime;
    String data;
}
