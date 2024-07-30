package ru.sparural.engine.models.nominatim;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class NominatimReverseResponse {

    private String addresstype;
    private String name;
}
