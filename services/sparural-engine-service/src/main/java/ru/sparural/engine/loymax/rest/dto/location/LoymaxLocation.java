package ru.sparural.engine.loymax.rest.dto.location;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
@FieldDefaults(level = AccessLevel.PRIVATE)
@ToString
public class LoymaxLocation {
    Long id;
    String locationId;
    String description;
    Double longitude;
    Double latitude;
    LoymaxLocationCity city;
    LoymaxLocationRegion region;
    String street;
    String house;
    @JsonProperty
    String building;
    @JsonProperty
    String office;
}
