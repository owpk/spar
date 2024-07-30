package ru.sparural.engine.api.dto.settings;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class AuthSettingDto {
    String secretKey;
}
