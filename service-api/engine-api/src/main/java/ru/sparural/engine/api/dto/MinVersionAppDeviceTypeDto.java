package ru.sparural.engine.api.dto;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@ToString
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class MinVersionAppDeviceTypeDto {
    Long id;
    String versionApp;
    String deviceTypeName;
    String marketUrl;
}
