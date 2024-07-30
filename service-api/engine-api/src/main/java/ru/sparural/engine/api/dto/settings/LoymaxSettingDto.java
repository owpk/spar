package ru.sparural.engine.api.dto.settings;

import lombok.*;
import lombok.experimental.FieldDefaults;

@NoArgsConstructor
@Getter
@Setter
@ToString
@FieldDefaults(level = AccessLevel.PRIVATE)
@Data
public class LoymaxSettingDto {
    String host;
    String username;
    String password;
    Long maxFaforiteCategoriesCount;
    String siteKey;
}
