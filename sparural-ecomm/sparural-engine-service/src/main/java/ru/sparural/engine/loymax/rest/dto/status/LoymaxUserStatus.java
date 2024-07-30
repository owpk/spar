package ru.sparural.engine.loymax.rest.dto.status;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.List;

/**
 * @author Vorobyev Vyacheslav
 */
@NoArgsConstructor
@Getter
@Setter
@ToString
@FieldDefaults(level = AccessLevel.PRIVATE)
@JsonIgnoreProperties(ignoreUnknown = true)
public class LoymaxUserStatus {
    String name;
    String counterUid;
    Integer currentValue;
    List<LoymaxUserStatusItem> statuses;
    LoymaxUserStatusItem currentStatus;
}
