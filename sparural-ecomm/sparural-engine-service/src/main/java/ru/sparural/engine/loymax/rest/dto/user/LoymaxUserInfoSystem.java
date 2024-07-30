package ru.sparural.engine.loymax.rest.dto.user;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.*;
import lombok.experimental.FieldDefaults;

/**
 * <a href="https://docs.loymax.net/xwiki/bin/view/Main/Integration/Ways_to_use_API/System_Api_Methods/Methods_of_system_api/User/#H41243E43743244043044943043544243843D44443E44043C43044643844E43E43A43B43843543D44243543F43E43D43E43C43544044344243543B43544443E43D430">...</a>
 */
@Getter
@Setter
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
@FieldDefaults(level = AccessLevel.PRIVATE)
@ToString
public class LoymaxUserInfoSystem {
    Long id;
    String uid;
    String firstName;
    String lastName;
    String patronymicName;
    LoymaxLoginInfo phone;
    LoymaxLoginInfo email;
    String birthDay;
    String addressInfo;
    String cardShortInfo;
    Boolean rejectPaperChecks;
    String registrationDate;
    Boolean isRequiredActions;
    String state; //<!--Состояние клиента: Normal — не зарегистрирован, Registered — зарегистрирован, Anonymous — анонимный, Deleted — удален, Deregistered — отказался от участия в ПЛ.-->
    String subscribeToAllAcceptDate; // "2021-03-31T06:59:42.281Z", <!--Дата подписки на все рассылки.-->
    String tenderOfferAcceptDate; // "2019-12-05T00:00:00Z", <!--Дата принятия оферты.-->
    String creationDate; // "2019-08-13T00:00:00Z", <!--Дата создания-->
    String loyaltyProgramName; // "string", <!--Название программы лояльности.-->
    Long loyaltyProgramId; // 0 <!--Внутренний идентификатор Программы лояльности.-->
}
