package ru.sparural.notification.service.impl.devino.dto.email;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.List;

/**
 * @author Vorobyev Vyacheslav
 */
@Getter
@Setter
@ToString
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@JsonIgnoreProperties(ignoreUnknown = true)
public class DevinoEmailPushRequestDto {

    @JsonProperty("Recipients")
    List<Recipient> recipients;

    @JsonProperty("CheckUnsubscription")
    Boolean checkUnsubscription;

    @JsonProperty("Sender")
    Sender sender;

    @JsonProperty("Subject")
    String subject;

    @JsonProperty("Body")
    Body body;

    @JsonProperty("AttachmentsIds")
    List<String> attachmentsIds;
}
