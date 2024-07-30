package ru.sparural.rest.api.notifications;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@ToString
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class NotificationsTypesRestDto {
    Long id;
    String name;
}
