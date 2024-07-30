package ru.sparural.engine.api.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.*;
import lombok.experimental.FieldDefaults;

/**
 * @author Vorobyev Vyacheslav
 */
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@JsonIgnoreProperties(ignoreUnknown = true)
public class NotificationFullDto {
    Long id;
    String title;
    String body;
    Long sendedAt;
    Boolean isReaded;
    String type;
    Long userId;
    ScreenDto screen;
    MerchantDto merchant;
}
