package ru.sparural.notification.service.impl.huawei;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * @author Vorobyev Vyacheslav
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class HuaweiServerToken {
    private String accessToken;
    private Long expiresIn;
}
