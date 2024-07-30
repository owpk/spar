package ru.sparural.engine.api.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Builder
@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CouponEmissionsDto {
    @JsonProperty
    Long id;
    String title;
    @JsonIgnore
    Long start;
    Long end;
    FileDto photo;
    Boolean isPublic;
}
