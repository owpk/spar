package ru.sparural.engine.api.dto.socials;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@ToString
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class SocialDto {
    Long id;
    String name;
    String appId;
    String appSecret;
}
