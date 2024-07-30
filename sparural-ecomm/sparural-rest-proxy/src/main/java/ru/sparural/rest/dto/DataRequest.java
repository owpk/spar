package ru.sparural.rest.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import javax.validation.Valid;

/**
 * @author Vorobyev Vyacheslav
 */
@NoArgsConstructor
@Setter
@Getter
@AllArgsConstructor
@Builder
public class DataRequest<T> {
    @Valid
    private T data;
    @JsonProperty
    private Integer version;
}
