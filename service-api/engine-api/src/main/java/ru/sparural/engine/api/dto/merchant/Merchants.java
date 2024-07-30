package ru.sparural.engine.api.dto.merchant;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.List;

/**
 * @author Vorobyev Vyacheslav
 */
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@JsonIgnoreProperties(ignoreUnknown = true)
@ToString
public class Merchants {
    Long id;
    String title;
    String address;
    Double longitude;
    Double latitude;
    Format format;
    String workingHoursFrom;
    String workingHoursTo;
    String workingStatus;
    String status;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    String closeUntilUntil;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    Integer distance;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    List<Attribute> attributes;
    String loymaxLocationId;
    Boolean isPublic;
}