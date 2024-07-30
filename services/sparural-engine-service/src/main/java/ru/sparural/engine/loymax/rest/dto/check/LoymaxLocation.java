package ru.sparural.engine.loymax.rest.dto.check;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.FieldDefaults;
import ru.sparural.engine.api.dto.merchant.Attribute;
import ru.sparural.engine.api.dto.merchant.Format;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
@FieldDefaults(level = AccessLevel.PRIVATE)
@ToString
public class LoymaxLocation {
    String locationId;
    Long id;
    String title;
    String description;
    Double longitude;
    Double latitude;
    Format format;
    String workingHoursFrom;
    String workingHoursTo;
    String status;
    String closeUntilUntil;
    Integer distance;
    List<Attribute> attributes;
    Boolean isPublic;
}
