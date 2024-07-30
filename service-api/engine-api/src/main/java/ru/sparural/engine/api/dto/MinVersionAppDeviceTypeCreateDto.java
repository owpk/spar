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
public class MinVersionAppDeviceTypeCreateDto {
    String versionApp;
    Long deviceTypeId;
    String marketUrl;
}
