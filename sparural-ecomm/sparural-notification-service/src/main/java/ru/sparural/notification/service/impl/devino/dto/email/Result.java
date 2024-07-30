package ru.sparural.notification.service.impl.devino.dto.email;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;
import lombok.experimental.FieldDefaults;

/**
 * @author Vorobyev Vyacheslav
 */
@Getter
@Setter
@ToString
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@JsonIgnoreProperties(ignoreUnknown = true)
public class Result {

    @JsonProperty("index")
    String index;

    @JsonProperty("Address")
    String address;

    @JsonProperty("MessageId")
    String messageId;

    @JsonProperty("Code")
    String code;
}
