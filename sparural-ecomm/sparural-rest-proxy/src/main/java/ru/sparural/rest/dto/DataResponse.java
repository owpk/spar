package ru.sparural.rest.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;
import lombok.experimental.FieldDefaults;
import ru.sparural.rest.utils.Constants;

@Setter
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
@FieldDefaults(level = AccessLevel.PRIVATE)
public class DataResponse<T> {
    Boolean success;
    T data;
    Integer version;
    Object meta;

    public DataResponse(T data) {
        this(data, true);
    }

    public DataResponse(T data, boolean success) {
        this.data = data;
        this.success = success;
        version = Constants.VERSION;
    }

}