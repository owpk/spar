package ru.sparural.engine.api.dto.user;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * @author Vorobyev Vyacheslav
 */
@Getter
@Setter
@NoArgsConstructor
public class UserPushTokenDto {
    private Long   id;
    private Long   userid;
    private String devicetype;
    private String token;
}
