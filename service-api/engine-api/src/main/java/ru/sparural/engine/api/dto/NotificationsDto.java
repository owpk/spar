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
public class NotificationsDto {
    Long id;
    String title;
    String body;
    Long sendedAt;
    Boolean isReaded;
    String type;
    Long userId;
    Long screenId;
    Long merchantId;
}
