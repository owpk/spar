package ru.sparural.engine.api.dto;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.List;

@Getter
@Setter
@ToString
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class NotificationsListWithMetaDto {
    List<NotificationFullDto> data;
    MetaForNotification meta;
}
