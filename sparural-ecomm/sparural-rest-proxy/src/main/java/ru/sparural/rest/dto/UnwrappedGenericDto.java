package ru.sparural.rest.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonUnwrapped;
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
public class UnwrappedGenericDto<T> {
    @JsonProperty
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private Integer version;
    @JsonUnwrapped
    @Valid
    private T data;
    @JsonProperty
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private Boolean success;
    @JsonProperty
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private Integer code;
}
