package ru.sparural.engine.loymax.rest.dto.user;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;
import lombok.experimental.FieldDefaults;

/**
 * @author Vorobyev Vyacheslav
 */
@Getter
@Setter
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
@FieldDefaults(level = AccessLevel.PRIVATE)
@ToString
public class LoymaxUserInfo {
    Long id;
    String firstName;
    String lastName;
    String patronymicName;
    String phoneNumber;
    String email;
    String personUid;
    String birthDay;
    String state;
    String addressInfo;
    String cardShortInfo;
    Boolean rejectPaperChecks;
    @JsonProperty
    String gender;
}
