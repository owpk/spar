package ru.sparural.triggerapi.dto;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.io.Serializable;

@Getter
@Setter
@ToString
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class MessageTypeDto implements Serializable {
    Long id;
    String name;
}
